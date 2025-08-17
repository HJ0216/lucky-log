/**
 * FortuneResultPage
 * 운세 결과 페이지의 UI/UX를 관리하는 모듈 객체
 * - 로딩 화면 전환
 * - 클립보드 복사 기능
 * - 결과 저장 및 공유
 * - 애니메이션 처리
 */
const FortuneResultPage = {
  // 설정값
  config: {
    loadingDuration: 3000, // 로딩 화면 표시 시간 (ms)
    fadeoutDuration: 300, // 페이드아웃 애니메이션 시간 (ms)
    animationDelay: 100, // 애니메이션 트리거 지연 시간 (ms)
    selectionPageUrl: "/"
  },

  // 메시지 상수
  messages: {
    copySuccess: "운세 결과가 복사되었습니다! 📋",
    shareSuccess: "공유 링크가 복사되었습니다! 🔗",
    saveSuccess: "운세가 저장되었습니다! 나중에 정확도를 확인해보세요 🔮",
    copyFailed: "복사에 실패했습니다. 😅",
    shareFailed: "공유에 실패했습니다. 😅",
    saveFailed: "저장에 실패했습니다. 😅",
    loginRequired: "로그인 페이지로 이동합니다! 🔐",
    loginConfirm: "저장하려면 로그인이 필요합니다.\n로그인 페이지로 이동하시겠습니까?"
  },

  // DOM 요소 캐싱
  elements: {
    resultsScreen: null,
    shareText: null,
    saveButton: null,
    fortuneSections: []
  },

  /**
   * 모듈 초기화 메서드
   * 페이지 로드 시 호출되어 모든 기능을 활성화
   */
  init() {
    this.cacheElements();
    this.showResults();
  },

  /**
   * 필요한 DOM 요소를 찾아 캐싱
   */
  cacheElements() {
    this.elements.resultsScreen = document.getElementById("results-screen");
    this.elements.shareText = document.getElementById("shareText");
    this.elements.saveButton = document.querySelector("[data-login-required]");
    this.elements.fortuneSections = document.querySelectorAll(".fortune-section");
    this.elements.fortuneContent = document.querySelector('.fortune-content');
  },

  showResults() {
    if (!this.elements.resultsScreen) return;

    setTimeout(() => {
      this.formatFortuneText();

      // 애니메이션 트리거
      this.triggerAnimations();
    }, this.config.loadingDuration);
  },

  /**
   * 결과 섹션 애니메이션 트리거
   */
   formatFortuneText() {
    if (!this.elements.fortuneContent) return;

    const text = this.elements.fortuneContent.textContent;
    const formatted = text.replace(/\|/g, '');
    this.elements.fortuneContent.textContent = formatted;
  },

  /**
   * 결과 섹션 애니메이션 트리거
   */
  triggerAnimations() {
    setTimeout(() => {
      this.elements.fortuneSections.forEach((section) => {
        section.style.animationPlayState = "running";
      });
    }, this.config.animationDelay);
  },

  /**
   * 운세 선택 페이지로 이동
   */
  goToSelection() {
    window.location.href = this.config.selectionPageUrl;
  },

  async copyResults() {
    // TODO: 구현 예정
  },

  async shareResults() {
    // TODO: 구현 예정
  },

  saveResults() {
    // TODO: 구현 예정
  },
};

// 전역 함수 노출 (HTML onclick 이벤트용)
window.goToSelection = () => FortuneResultPage.goToSelection();
window.copyResults = () => FortuneResultPage.copyResults();
window.shareResults = () => FortuneResultPage.shareResults();
window.saveResults = () => FortuneResultPage.saveResults();

// 페이지의 모든 DOM 콘텐츠가 로드된 후 모듈을 초기화
document.addEventListener("DOMContentLoaded", () => {
  FortuneResultPage.init();
});