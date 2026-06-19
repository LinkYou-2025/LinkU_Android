const fetch = require("node-fetch");

const GITHUB_TOKEN = process.env.PERSONAL_TOKEN;
const DISCORD_WEBHOOK = process.env.DISCORD_WEBHOOK;

const ORG = "LinkYou-2025";
const REPO = "LinkU_Android";

const MEMBERS = {
  ugmin1030: { name: "지민", style: "t" },
  Hongji03: { name: "지현", style: "f" },
//  KateteDeveloper: { name: "윤지", style: "t2" },
  codebidoof: { name: "현우", style: "t3" }, // 오늘부터 t 합시다
};

// 다인 언니 코드 훔침.
// 안정적인 KST 날짜 문자열 변환
function kstDateString(date = new Date()) {
  return new Intl.DateTimeFormat("en-CA", {
    timeZone: "Asia/Seoul",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).format(date);
}

// KST 하루를 정확히 UTC 범위로 변환
function getKstDayUtcRange(kstYYYYMMDD) {
  const [y, m, d] = kstYYYYMMDD.split("-").map(Number);
  // KST 00:00:00 = UTC 전날 15:00:00
  const since = new Date(Date.UTC(y, m - 1, d - 1, 15, 0, 0, 0));
  // KST 23:59:59 = UTC 당일 14:59:59
  const until = new Date(Date.UTC(y, m - 1, d, 14, 59, 59, 999));

  return {
    since: since.toISOString(),
    until: until.toISOString(),
  };
}

// 주간 통계를 위한 월요일 ~ 일요일 UTC 범위 구하기
function getThisWeekUtcRange() {
  const now = new Date();
  // 현재 시간의 KST 날짜 구하기
  const todayKst = kstDateString(now);

  // 기준일의 KST 자정 객체 생성
  const currentKst = new Date(`${todayKst}T00:00:00+09:00`);
  const day = currentKst.getDay(); // 0: 일, 1: 월, ...
  const diffToMonday = day === 0 ? 6 : day - 1;

  // 이번주 월요일 KST
  const mondayKst = new Date(currentKst);
  mondayKst.setDate(currentKst.getDate() - diffToMonday);
  const mondayStr = kstDateString(mondayKst);

  // 이번주 일요일 KST
  const sundayKst = new Date(mondayKst);
  sundayKst.setDate(mondayKst.getDate() + 6);
  const sundayStr = kstDateString(sundayKst);

  const rangeStart = getKstDayUtcRange(mondayStr);
  const rangeEnd = getKstDayUtcRange(sundayStr);

  return {
    since: rangeStart.since,
    until: rangeEnd.until,
    label: `${mondayStr} ~ ${sundayStr}`,
  };
}

// 다시 다인 언니 코드 훔침
async function gh(url) {
  const res = await fetch(url, {
    headers: {
      Authorization: `Bearer ${GITHUB_TOKEN}`,
      Accept: "application/vnd.github+json",
      "User-Agent": "linku-android-bot",
    },
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`GitHub API ${res.status}: ${text}`);
  }
  return res.json();
}

async function getAllPages(url) {
  let page = 1;
  let allData = [];
  const separator = url.includes("?") ? "&" : "?";

  while (true) {
    const data = await gh(`${url}${separator}per_page=100&page=${page}`).catch(() => []);
    if (!Array.isArray(data) || data.length === 0) break;
    allData = allData.concat(data);
    if (data.length < 100) break;
    page++;
  }
  return allData;
}

// 점수 계산 공식
function calcScore({ commits, prs, reviews, issues }) {
  return commits * 3 + prs * 5 + reviews * 2 + issues * 1;
}

function getPraiseMessage(login, stats) {
  const { name, style } = MEMBERS[login];
  const score = calcScore(stats);

  switch (style) {
    case "f":
      if (score >= 15) return `🌸 ${name}아 오늘 너무 열심히 했잖아ㅠㅠ 커밋 ${stats.commits}개에 PR도 ${stats.prs}개라니.. 진짜 링큐 없어서는 안 될 존재야💖`;
      if (score >= 5)  return `☺️ ${name}아 오늘도 묵묵하게 해줬구나🥹 커밋 ${stats.commits}개 확인했어, 너무 수고했어!!`;
      return `🥺 ${name}아.. 오늘 좀 힘들었어? 괜찮아, 내일 더 일을 하면 되지~`;
    case "t":
      if (score >= 15) return `🔍 ${name} 오늘 커밋 ${stats.commits}개 전부 한 줄 한 줄 꼼꼼하게 짠 거 다 보여. PR ${stats.prs}개까지.. 총점 ${score}점. 코드 퀄리티는 역시 지민이지.`;
      if (score >= 5)  return `📐 ${name} 오늘 점수 ${score}점. 커밋 ${stats.commits}개, 허투루 짠 코드 하나도 없는 거 알고 있음. 꼼꼼함이 팀의 기반이야. 진짜 안드 팀장이 최고다. 항상 믿는 거 알지?`;
      return `📋 ${name} 오늘 커밋 ${stats.commits}개. 한 줄 한 줄 신중하게 썼을 거라 믿고 있지. 내일 더 일해보자~!`;
//    case "t2":
//      if (score >= 15) return `🔥 윤지 오늘 커밋 ${stats.commits}개. 코드 한 줄 치기 전에 다 이해하고 짜는 거 티 나. PR ${stats.prs}개까지.. 총점 ${score}점. 팀원 중에 제일 꼼꼼한 거 인정.`;
//      if (score >= 5)  return `💻 윤지 오늘 점수 ${score}점. 커밋 ${stats.commits}개, 대충 짠 코드 없는 거 알고 있음. 오늘도 링큐 지탱해줘서 고마워.`;
//      return `🌱 윤지 오늘 커밋 ${stats.commits}개. 오늘은 조용했네. 생각하면서 코딩하는 스타일인 거 알아서 믿고 있음. 내일 보여줘.`;
    case "t3":
      if (score >= 15) return `⚡ 현우 오늘 커밋 ${stats.commits}개. 새로 합류했는데 이 정도면 진짜 레전드.. PR ${stats.prs}개까지, 총점 ${score}점. 링큐가 든든해졌다!`;
      if (score >= 5)  return `🛠 현우 오늘 점수 ${score}점. 커밋 ${stats.commits}개, 적응하면서도 꾸준히 해줘서 고마워. 팀이 너 믿고 있어!`;
      return `🌀 현우 오늘 커밋 ${stats.commits}개. 명조 대신 링큐해주네 감동이야`;
    default:
      return `오늘도 수고했어요!`;
  }
}

function getScoldMessage(login) {
  const { name, style } = MEMBERS[login];
  switch (style) {
    case "f":  return `😢 ${name}아... 3일째 커밋이 없어ㅠㅠ 혹시 많이 힘들어? 그래도 링큐는 너를 기다리고 있어.. 제발 돌아와줘🙏`;
    case "t":  return `🚨 ${name} 3일 연속 커밋이 없습니다.... 팀장님 조금만 더 힘내주세요...`;
//    case "t2": return `📵 윤지 3일 연속 커밋 0건. 분석하느라 바쁜 거 알겠는데.. 커밋 한 개만이라도 부탁해요🥺`;
    case "t3": return `🆕 현우 3일 연속 커밋 0건. 명조 하는거야?`;
    default:   return `${name} 3일째 커밋 없음. 확인 바람.`;
  }
}

async function run() {
  console.log("===== LINKU STATS START =====");

  const isWeekly = process.env.WEEKLY === "true";
  const now = new Date();
  const todayKstStr = kstDateString(now);

  let since, until, dateLabel;

  if (isWeekly) {
      const weekRange = getThisWeekUtcRange();
      since = weekRange.since;
      until = weekRange.until;
      dateLabel = weekRange.label;
  } else {
      const dayRange = getKstDayUtcRange(todayKstStr);
      since = dayRange.since;
      until = dayRange.until;
      dateLabel = todayKstStr;
  }

  console.log(`모드: ${isWeekly ? "주간" : "일간"}, 기간: ${since} ~ ${until}`);

  // 1. 기초 데이터 서치 인프라 구동 (브랜치, PR, 이슈 목록 전부 긁어오기)
  const branches = await gh(`https://api.github.com/repos/${ORG}/${REPO}/branches?per_page=100`).catch(() => []);
  const allPRs = await getAllPages(`https://api.github.com/repos/${ORG}/${REPO}/pulls?state=all`);
  const allIssues = await getAllPages(`https://api.github.com/repos/${ORG}/${REPO}/issues?state=all`);

  const statsMap = {};
  for (const login of Object.keys(MEMBERS)) {
    statsMap[login] = { commits: 0, prs: 0, reviews: 0, issues: 0 };
  }

  // 2. [커밋 집계] 모든 브랜치 순회하며 중복 없이 산출
  const seenSha = new Set();
  const commitPromises = [];

  for (const b of branches) {
    for (const login of Object.keys(MEMBERS)) {
      const url = `https://api.github.com/repos/${ORG}/${REPO}/commits?sha=${encodeURIComponent(b.name)}&since=${since}&until=${until}&author=${login}&per_page=100`;
      commitPromises.push(
        gh(url)
          .then(commits => ({ login, commits }))
          .catch(() => ({ login, commits: [] }))
      );
    }
  }

  const commitResults = await Promise.all(commitPromises);

  for (const res of commitResults) {
      if (!Array.isArray(res.commits)) continue;
      for (const c of res.commits) {
        if (!c?.sha) continue;
        if (seenSha.has(c.sha)) continue;
        seenSha.add(c.sha);

        if (c.parents && c.parents.length > 1) continue;

        statsMap[res.login].commits++;
      }
    }

  const sinceDate = new Date(since);
  const untilDate = new Date(until);

  allPRs.forEach(pr => {
      const login = pr.user?.login;
      if (!MEMBERS[login]) return;
      const created = new Date(pr.created_at);
      if (created < sinceDate || created > untilDate) return;
      statsMap[login].prs++;
  });

  allIssues.forEach(issue => {
    const created = new Date(issue.created_at);
    const login = issue.user?.login;
    if (MEMBERS[login] && !issue.pull_request && created >= sinceDate && created <= untilDate) {
      statsMap[login].issues++;
    }
  });

  // 비동기 처리 병렬 최적화 (Promise.all 활용하여 대기속도 향상)
  await Promise.all(
    allPRs.map(async (pr) => {
      const prUpdated = new Date(pr.updated_at);
      if (prUpdated < sinceDate) return;

      try {
        const reviews = await gh(`https://api.github.com/repos/${ORG}/${REPO}/pulls/${pr.number}/reviews`).catch(() => []);
        if (!Array.isArray(reviews)) return;

        reviews.forEach(r => {
          const submitted = new Date(r.submitted_at);
          const login = r.user?.login;
          if (MEMBERS[login] && submitted >= sinceDate && submitted <= untilDate) {
            statsMap[login].reviews++;
          }
        });
      } catch (e) {
        console.error(`PR #${pr.number} 리뷰 매핑 실패`);
      }
    })
  );

  // 4. [3일 연속 커밋 여부 체크] (일간 모드일 때만 구동)
  const idleMembers = [];
  if (!isWeekly) {
    await Promise.all(
      Object.keys(MEMBERS).map(async (login) => {
        // 오늘(0), 어제(1), 그저께(2) 범위 미리 계산
        const dayRanges = [0, 1, 2].map((i) => {
          const targetDate = new Date(now.getTime());
          targetDate.setDate(now.getDate() - i);
          return getKstDayUtcRange(kstDateString(targetDate));
        });

        // 멤버별 3일치 × 모든 브랜치 병렬 조회
        const results = await Promise.all(
          dayRanges.flatMap((range) =>
            branches.map((b) =>
              gh(
                `https://api.github.com/repos/${ORG}/${REPO}/commits?sha=${encodeURIComponent(b.name)}&since=${range.since}&until=${range.until}&author=${login}&per_page=1`
              ).catch(() => [])
            )
          )
        );

        // 하나라도 커밋 있으면 활성으로 판단
        const hasCommitIn3Days = results.some(
          (commits) => Array.isArray(commits) && commits.length > 0
        );

        if (!hasCommitIn3Days) {
          idleMembers.push(login);
        }
      })
    );
  }

  // 5. 랭킹 정렬 및 메시지 포맷팅
  const sorted = Object.keys(MEMBERS).map(login => ({
    login,
    stats: statsMap[login],
    score: calcScore(statsMap[login])
  })).sort((a, b) => b.score - a.score);

  const medals = ["👑", "🥈", "🥉", "🎖"];
  let message = isWeekly
    ? `📅 **${dateLabel} 링큐 Android 주간 랭킹!**\n\n`
    : `🏆 **${dateLabel} (KST) 오늘의 링큐 기여왕!**\n\n`;

  sorted.forEach((r, i) => {
    const { name } = MEMBERS[r.login];
    message += `${medals[i] || "🎖"} **${i + 1}위 ${name}** — `;
    message += `커밋 ${r.stats.commits} / PR ${r.stats.prs} / 리뷰 ${r.stats.reviews} / 이슈 ${r.stats.issues} (${r.score}점)\n`;
    message += getPraiseMessage(r.login, r.stats) + "\n\n";
  });

  if (idleMembers.length > 0) {
    message += `\n━━━━━━━━━━━━━━━━━━━━━━\n🚨 **3일 이상 커밋 없음.. 다인 언니가 슬퍼하고 있어요**\n\n`;
    idleMembers.forEach(login => {
      message += getScoldMessage(login) + "\n";
    });
  }

  message += `\n링큐 화이팅!! 오늘도 앱 완성을 향해 🚀`;

  // 6. 디스코드 발송
  await fetch(DISCORD_WEBHOOK, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ content: message }),
  });

  console.log("최종 집계 데이터:", statsMap);
  console.log("디스코드 알림 전송 완료!");
  console.log("===== LINKU STATS END =====");
}

run().catch((e) => {
  console.error("실행 오류:", e);
  process.exit(1);
});