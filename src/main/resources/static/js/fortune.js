// 운세 종류별 아이콘 매핑
const fortuneIcons = {
  overall: "🔮",
  money: "💰",
  love: "💕",
  career: "💼",
  study: "📚",
  luck: "🍀",
  family: "🏠",
  health: "💪",
};

// 운세 종류별 이름 매핑
const fortuneNames = {
  overall: "종합운",
  money: "재물운",
  love: "애정운",
  career: "직장·사업운",
  study: "학업·시험운",
  luck: "행운",
  family: "가정운",
  health: "건강운",
};

// AI 정보 매핑
const aiInfo = {
  claude: { icon: "🎆", name: "Claude" },
  gemini: { icon: "🪂", name: "Gemini" },
  gpt: { icon: "🚀", name: "GPT" },
};

// 더미 운세 데이터 (실제로는 API에서 받아옴)
const dummyFortuneData = {
  overall:
    "전반적으로 안정된 기운이 흐르는 시기입니다. 새로운 시작을 위한 준비를 하기에 좋은 때이며, 과거의 경험을 바탕으로 현명한 선택을 할 수 있을 것입니다.",
  money:
    "금전적으로는 꾸준한 흐름을 보이는 시기입니다. 무리한 투자보다는 안정적인 저축이나 계획적인 소비가 도움이 될 것입니다.",
  love: "사랑에 있어서는 진실한 마음이 통하는 시기입니다. 상대방을 이해하려는 노력과 소통이 관계 발전의 열쇠가 될 것입니다.",
  career:
    "직장이나 사업에서는 새로운 기회가 찾아올 가능성이 높습니다. 준비된 자에게 행운이 따르니, 역량 개발에 힘쓰시기 바랍니다.",
  study:
    "학습이나 시험 준비에는 집중력이 필요한 시기입니다. 체계적인 계획을 세우고 꾸준히 노력한다면 좋은 결과를 얻을 수 있을 것입니다.",
  luck: "행운의 기운이 서서히 다가오고 있습니다. 작은 기회들을 놓치지 말고, 긍정적인 마음가짐을 유지하는 것이 중요합니다.",
  family:
    "가족 관계에서는 화합과 이해가 중요한 시기입니다. 서로에 대한 배려와 관심이 가정의 평화를 가져다줄 것입니다.",
  health:
    "건강 관리에 특별한 주의가 필요한 시기입니다. 규칙적인 생활 패턴과 적절한 운동, 균형 잡힌 식단을 유지하시기 바랍니다.",
};

// 페이지 로드 시 초기화
document.addEventListener("DOMContentLoaded", function () {
  // URL 파라미터에서 선택 정보 가져오기 (실제로는 이전 페이지에서 전달받음)
  const selectedAI = "claude"; // 예시
  const selectedFortunes = ["overall", "money", "love", "career"]; // 예시
  const selectedPeriod = "monthly"; // 예시

  // AI 정보 표시
  updateAIInfo(selectedAI);

  // 로딩 시작
  startLoading(selectedFortunes, selectedPeriod);
});

function updateAIInfo(ai) {
  const aiIcon = document.getElementById("ai-icon");
  const aiName = document.getElementById("ai-name");

  aiIcon.textContent = aiInfo[ai].icon;
  aiName.textContent = aiInfo[ai].name;
}

function startLoading(fortunes, period) {
  // 3초 후 결과 표시 (실제로는 API 응답을 기다림)
  setTimeout(() => {
    showResults(fortunes, period);
  }, 3000);
}

function showResults(fortunes, period) {
  const loadingScreen = document.getElementById("loading-screen");
  const resultsScreen = document.getElementById("results-screen");
  const resultsContainer = document.getElementById("results-container");

  // 로딩 화면 숨기기
  loadingScreen.style.display = "none";

  // 결과 생성
  resultsContainer.innerHTML = "";

  fortunes.forEach((fortuneType, index) => {
    const section = createFortuneSection(fortuneType, period);
    resultsContainer.appendChild(section);
  });

  // 결과 화면 표시
  resultsScreen.style.display = "flex";
  resultsScreen.style.flexDirection = "column";
}

function createFortuneSection(fortuneType, period) {
  const section = document.createElement("div");
  section.className = "fortune-section";

  const periodText =
    period === "monthly"
      ? "월별"
      : period === "quarterly"
      ? "분기별"
      : "올 한해";

  section.innerHTML = `
                <div class="fortune-title">
                    <span class="fortune-icon">${fortuneIcons[fortuneType]}</span>
                    <span>${fortuneNames[fortuneType]}</span>
                </div>
                <div class="fortune-content-wrapper">
                    <div class="fortune-content">
                        ${dummyFortuneData[fortuneType]}
                    </div>
                </div>
            `;

  return section;
}

function goToSelection() {
  // 실제로는 이전 페이지로 이동하면서 기존 선택 값들을 유지
  alert(
    "🔄 선택 화면으로 이동합니다!\n기존 설정을 유지한 채로 다시 선택할 수 있습니다."
  );
  // window.location.href = 'index.html?preset=true';
}

function copyResults() {
  const resultsText = generateShareText();

  if (navigator.clipboard) {
    navigator.clipboard
      .writeText(resultsText)
      .then(() => {
        showToast("운세 결과가 복사되었습니다! 📋");
      })
      .catch(() => {
        showToast("복사에 실패했습니다. 😅");
      });
  } else {
    // 구형 브라우저 대응
    const textArea = document.createElement("textarea");
    textArea.value = resultsText;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand("copy");
    document.body.removeChild(textArea);
    showToast("운세 결과가 복사되었습니다! 📋");
  }
}

function shareResults() {
  // URL 공유 기능 (추후 구현)
  const shareUrl = window.location.href; // 현재 URL 또는 파라미터 포함된 URL

  if (navigator.clipboard) {
    navigator.clipboard
      .writeText(shareUrl)
      .then(() => {
        showToast("공유 링크가 복사되었습니다! 🔗");
      })
      .catch(() => {
        showToast("공유에 실패했습니다. 😅");
      });
  } else {
    const textArea = document.createElement("textarea");
    textArea.value = shareUrl;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand("copy");
    document.body.removeChild(textArea);
    showToast("공유 링크가 복사되었습니다! 🔗");
  }
}

function saveResults() {
  // 로그인 체크 로직 (추후 구현)
  const isLoggedIn = false; // 실제로는 로그인 상태 확인

  if (!isLoggedIn) {
    if (
      confirm(
        "저장하려면 로그인이 필요합니다.\n로그인 페이지로 이동하시겠습니까?"
      )
    ) {
      // window.location.href = '/login';
      showToast("로그인 페이지로 이동합니다! 🔐");
    }
    return;
  }

  const resultsData = {
    timestamp: new Date().toISOString(),
    ai: document.getElementById("ai-name").textContent,
    userInfo: document.getElementById("user-info").textContent,
    results: generateShareText(),
  };

  try {
    let savedResults = JSON.parse(
      localStorage.getItem("fortuneResults") || "[]"
    );
    savedResults.unshift(resultsData);

    if (savedResults.length > 10) {
      savedResults = savedResults.slice(0, 10);
    }

    localStorage.setItem("fortuneResults", JSON.stringify(savedResults));
    showToast("운세가 저장되었습니다! 나중에 정확도를 확인해보세요 🔮");
  } catch (error) {
    showToast("저장에 실패했습니다. 😅");
  }
}

function generateShareText() {
  const aiName = document.getElementById("ai-name").textContent;
  const userInfo = document.getElementById("user-info").textContent;
  const sections = document.querySelectorAll(".fortune-section");

  let shareText = `🔮 ${aiName} AI 운세 결과\n\n👤 ${userInfo}\n\n`;

  sections.forEach((section) => {
    const title = section.querySelector(
      ".fortune-title span:nth-child(2)"
    ).textContent;
    const period = section.querySelector(".fortune-period").textContent;
    const content = section.querySelector(".fortune-content").textContent;

    shareText += `${title} (${period})\n${content}\n\n`;
  });

  shareText += "✨ Y2K 운세에서 확인하세요!";

  return shareText;
}

function showToast(message) {
  alert(message); // 토스트 대신 alert 사용
}

// 키보드 단축키
document.addEventListener("keydown", function (e) {
  if (e.key === "Escape") {
    goToSelection();
  } else if (e.ctrlKey && e.key === "s") {
    e.preventDefault();
    saveResults();
  } else if (e.ctrlKey && e.shiftKey && e.key === "S") {
    e.preventDefault();
    shareResults();
  }
});

// 스크롤 애니메이션 (결과가 보이면 추가 애니메이션)
function observeScrollAnimations() {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.style.animationPlayState = "running";
        }
      });
    },
    {
      threshold: 0.1,
    }
  );

  document.querySelectorAll(".fortune-section").forEach((section) => {
    observer.observe(section);
  });
}

// 결과 표시 후 스크롤 애니메이션 관찰 시작
setTimeout(() => {
  if (document.getElementById("results-screen").style.display === "block") {
    observeScrollAnimations();
  }
}, 4000);
