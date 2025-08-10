// 최소한의 클라이언트 사이드 검증 및 UX 개선
document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector("form");
  const submitBtn = document.querySelector(".retro-btn");

  // 폼 제출 시 버튼 비활성화 (중복 제출 방지)
  if (form && submitBtn) {
    form.addEventListener("submit", function () {
      submitBtn.disabled = true;
      submitBtn.textContent = "⏳ 운세 생성 중...";
    });
  }

  // 에러 메시지 자동 숨기기 (5초 후)
  const errorMessages = document.querySelectorAll(".error-message, .alert");
  errorMessages.forEach(function (errorMsg) {
    if (errorMsg.textContent.trim()) {
      setTimeout(function () {
        errorMsg.style.opacity = "0";
        setTimeout(function () {
          errorMsg.style.display = "none";
        }, 300);
      }, 5000);
    }
  });

  // 비활성화된 옵션에 대한 안내 (선택적)
  const disabledInputs = document.querySelectorAll("input:disabled");
  disabledInputs.forEach(function (input) {
    const label = input.closest(".ai-option, .fortune-option, .period-option");
    if (label) {
      label.style.opacity = "0.5";
      label.title = "준비 중인 기능입니다";
    }
  });
});
