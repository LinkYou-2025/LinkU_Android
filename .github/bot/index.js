const https = require("https");

const GITHUB_TOKEN = process.env.PERSONAL_TOKEN;
const DISCORD_WEBHOOK = process.env.DISCORD_WEBHOOK;
const ORG = "LinkYou-2025";
const REPO = "LinkU_Android";

const MEMBERS = {
  ugmin1030: { name: "지민", style: "t" },
  Hongji03: { name: "지현", style: "f" },
  KateteDeveloper: { name: "윤지", style: "t2" },
};

function githubGet(path) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: "api.github.com",
      path,
      headers: {
        Authorization: `token ${GITHUB_TOKEN}`,
        "User-Agent": "linku-bot",
        Accept: "application/vnd.github+json",
      },
    };
    https.get(options, (res) => {
      let data = "";
      res.on("data", (chunk) => (data += chunk));
      res.on("end", () => {
        try { resolve(JSON.parse(data)); }
        catch (e) { reject(e); }
      });
    }).on("error", reject);
  });
}

function getKSTRange(daysAgo = 0) {
  const now = new Date();
  const kst = new Date(now.getTime() + 9 * 60 * 60 * 1000);
  kst.setUTCDate(kst.getUTCDate() - daysAgo);
  const dateStr = kst.toISOString().slice(0, 10);
  const since = new Date(`${dateStr}T00:00:00+09:00`).toISOString();
  const until = new Date(`${dateStr}T23:59:59+09:00`).toISOString();
  return { since, until, dateStr };
}

// 이번 주 월요일 00:00 ~ 일요일 23:59 (KST) 반환
function getThisWeekKSTRange() {
  const now = new Date();
  const kst = new Date(now.getTime() + 9 * 60 * 60 * 1000);
  const day = kst.getUTCDay(); // 0=일, 1=월 ... 6=토
  const diffToMonday = day === 0 ? 6 : day - 1;
  const monday = new Date(kst);
  monday.setUTCDate(kst.getUTCDate() - diffToMonday);
  const mondayStr = monday.toISOString().slice(0, 10);
  const sunday = new Date(monday);
  sunday.setUTCDate(monday.getUTCDate() + 6);
  const sundayStr = sunday.toISOString().slice(0, 10);
  return {
    since: new Date(`${mondayStr}T00:00:00+09:00`).toISOString(),
    until: new Date(`${sundayStr}T23:59:59+09:00`).toISOString(),
    label: `${mondayStr} ~ ${sundayStr}`,
  };
}

async function getCommitCount(login, since, until) {
  try {
    const branches = await githubGet(`/repos/${ORG}/${REPO}/branches`);
    const seenSHAs = new Set();
    let count = 0;
    for (const branch of branches) {
      const commits = await githubGet(
        `/repos/${ORG}/${REPO}/commits?author=${login}&since=${since}&until=${until}&sha=${branch.name}&per_page=100`
      );
      if (!Array.isArray(commits)) continue;
      for (const c of commits) {
        if (seenSHAs.has(c.sha)) continue;
        if (c.parents && c.parents.length >= 2) continue;
        seenSHAs.add(c.sha);
        count++;
      }
    }
    return count;
  } catch { return 0; }
}

async function getPRCount(login, since, until) {
  try {
    const prs = await githubGet(`/repos/${ORG}/${REPO}/pulls?state=all&per_page=100`);
    if (!Array.isArray(prs)) return 0;
    return prs.filter((pr) => {
      const created = new Date(pr.created_at);
      return pr.user.login === login && created >= new Date(since) && created <= new Date(until);
    }).length;
  } catch { return 0; }
}

async function getReviewCount(login, since, until) {
  try {
    const prs = await githubGet(`/repos/${ORG}/${REPO}/pulls?state=all&per_page=100`);
    if (!Array.isArray(prs)) return 0;
    let count = 0;
    for (const pr of prs) {
      const reviews = await githubGet(`/repos/${ORG}/${REPO}/pulls/${pr.number}/reviews`);
      if (!Array.isArray(reviews)) continue;
      count += reviews.filter((r) => {
        const submitted = new Date(r.submitted_at);
        return r.user.login === login && submitted >= new Date(since) && submitted <= new Date(until);
      }).length;
    }
    return count;
  } catch { return 0; }
}

async function getIssueCount(login, since, until) {
  try {
    const issues = await githubGet(`/repos/${ORG}/${REPO}/issues?state=all&since=${since}&per_page=100`);
    if (!Array.isArray(issues)) return 0;
    return issues.filter((i) => {
      const created = new Date(i.created_at);
      return i.user.login === login && !i.pull_request && created >= new Date(since) && created <= new Date(until);
    }).length;
  } catch { return 0; }
}

async function hasNoCommitFor3Days(login) {
  for (let i = 0; i < 3; i++) {
    const { since, until } = getKSTRange(i);
    const count = await getCommitCount(login, since, until);
    if (count > 0) return false;
  }
  return true;
}

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

    case "t2":
      if (score >= 15) return `🔥 윤지 오늘 커밋 ${stats.commits}개. 코드 한 줄 치기 전에 다 이해하고 짜는 거 티 나. PR ${stats.prs}개까지.. 총점 ${score}점. 팀원 중에 제일 꼼꼼한 거 인정.`;
      if (score >= 5)  return `💻 윤지 오늘 점수 ${score}점. 커밋 ${stats.commits}개, 대충 짠 코드 없는 거 알고 있음. 오늘도 링큐 지탱해줘서 고마워.`;
      return `🌱 윤지 오늘 커밋 ${stats.commits}개. 오늘은 조용했네. 생각하면서 코딩하는 스타일인 거 알아서 믿고 있음. 내일 보여줘.`;

    default:
      return `오늘도 수고했어요!`;
  }
}

function getScoldMessage(login) {
  const { name, style } = MEMBERS[login];
  switch (style) {
    case "f":  return `😢 ${name}아... 3일째 커밋이 없어ㅠㅠ 혹시 많이 힘들어? 그래도 링큐는 너를 기다리고 있어.. 제발 돌아와줘🙏`;
    case "t":  return `🚨 ${name} 3일 연속 커밋이 없습니다.... 팀장님 조금만 더 힘내주세요...`;
    case "t2": return `📵 윤지 3일 연속 커밋 0건. 분석하느라 바쁜 거 알겠는데.. 커밋 한 개만이라도 부탁해요🥺`;
    default:   return `${name} 3일째 커밋 없음. 확인 바람.`;
  }
}

function sendDiscord(content) {
  return new Promise((resolve, reject) => {
    const body = JSON.stringify({ content });
    const url = new URL(DISCORD_WEBHOOK);
    const options = {
      hostname: url.hostname,
      path: url.pathname + url.search,
      method: "POST",
      headers: { "Content-Type": "application/json", "Content-Length": Buffer.byteLength(body) },
    };
    const req = https.request(options, (res) => { res.on("data", () => {}); res.on("end", resolve); });
    req.on("error", reject);
    req.write(body);
    req.end();
  });
}

// 주간: 이번 주 월~일 한 번에 집계 (rate limit 절약)
async function getWeeklyStats(login) {
  const { since, until } = getThisWeekKSTRange();
  const [commits, prs, reviews, issues] = await Promise.all([
    getCommitCount(login, since, until),
    getPRCount(login, since, until),
    getReviewCount(login, since, until),
    getIssueCount(login, since, until),
  ]);
  return { commits, prs, reviews, issues };
}

async function main() {
  const isWeekly = process.env.WEEKLY === "true";
  const { since, until, dateStr } = getKSTRange(0);
  const { label: weekLabel } = getThisWeekKSTRange();
  const logins = Object.keys(MEMBERS);
  const results = [];

  for (const login of logins) {
    const stats = isWeekly
      ? await getWeeklyStats(login)
      : await Promise.all([
          getCommitCount(login, since, until),
          getPRCount(login, since, until),
          getReviewCount(login, since, until),
          getIssueCount(login, since, until),
        ]).then(([commits, prs, reviews, issues]) => ({ commits, prs, reviews, issues }));
    const score = calcScore(stats);
    const noCommit3Days = !isWeekly && (await hasNoCommitFor3Days(login));
    results.push({ login, stats, score, noCommit3Days });
  }

  results.sort((a, b) => b.score - a.score);
  const medals = ["👑", "🥈", "🥉"];

  let msg = isWeekly
    ? `📅 **${weekLabel} 링큐 Android 주간 랭킹!**\n\n`
    : `🏆 **${dateStr} (KST) 오늘의 링큐 기여왕!**\n\n`;

  results.forEach((r, i) => {
    const { name } = MEMBERS[r.login];
    msg += `${medals[i] || "🎖"} **${i + 1}위 ${name}** — `;
    msg += `커밋 ${r.stats.commits} / PR ${r.stats.prs} / 리뷰 ${r.stats.reviews} / 이슈 ${r.stats.issues} (${r.score}점)\n`;
    msg += getPraiseMessage(r.login, r.stats) + "\n\n";
  });

  const idleMembers = results.filter((r) => r.noCommit3Days);
  if (idleMembers.length > 0) {
    msg += `\n━━━━━━━━━━━━━━━━━━━━━━\n🚨 **3일 이상 커밋 없음.. 다인 언니가 슬퍼하고 있어요**\n\n`;
    idleMembers.forEach((r) => { msg += getScoldMessage(r.login) + "\n"; });
  }

  msg += `\n링큐 화이팅!! 오늘도 앱 완성을 향해 🚀`;
  await sendDiscord(msg);
  console.log("✅ 디스코드 전송 완료!");
}

main().catch((e) => { console.error("❌ 오류 발생:", e); process.exit(1); });
