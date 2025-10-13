'use strict';

const FortuneOptionPage = {
  config: {
    ANIMATION_DURATION: 300, // 0.3s
    ERROR_DURATION: 5000,
    OPTION_BACK_URL: '/fortune/option/back',
    OPTION_DISABLED_TOOLTIP: '준비 중인 기능입니다.',
  },

  // DOM 요소 캐싱
  elements: {
    loadingScreen: null,
    contentsScreen: null,
    form: null,
    submitBtn: null,
    errorMessages: [],
    disabledOptionContainers: [],
  },

  init() {
    this.cacheElements();
    if (!this.validateRequiredElements()) return;
    this.applyInitialStyles();
    this.attachEvents();
    this.autoHideErrors();
  },

  cacheElements() {
    this.elements.loadingScreen = document.querySelector('#loading-screen');
    this.elements.contentsScreen = document.querySelector('#contents-screen');
    this.elements.form = document.querySelector('form');
    this.elements.submitBtn = document.querySelector('[data-submit-btn]');
    this.elements.errorMessages = document.querySelectorAll(
      '[data-error-message]'
    );

    // 비활성화된 input을 감싸는 컨테이너 캐싱
    const containers = document.querySelectorAll(
      '.form-option-content-item:has(input:disabled)'
    );

    this.elements.disabledOptionContainers = Array.from(containers);
  },

  validateRequiredElements() {
    const required = ['loadingScreen', 'contentsScreen', 'form', 'submitBtn'];

    const missing = required.filter((key) => !this.elements[key]);
    if (missing.length > 0) {
      const message = `Missing required elements: ${missing.join(', ')}`;

      console.error(message);
      return false;
    }

    return true;
  },

  applyInitialStyles() {
    this.elements.disabledOptionContainers.forEach((container) => {
      container.classList.add('disabled-option');
      container.title = this.config.OPTION_DISABLED_TOOLTIP;
    });
  },

  attachEvents() {
    this.elements.form.addEventListener('submit', this.handleSubmit());
  },

  // error messages
  autoHideErrors() {
    this.elements.errorMessages.forEach((message) => {
      // 메시지에 내용이 있을 때만 타이머 작동
      if (!message.textContent.trim()) return;

      // fade-out 애니메이션이 끝난 후 display: none 처리
      setTimeout(() => {
        message.style.classList.add('hidden');
      }, this.config.ERROR_DURATION);
    });
  },

  handleSubmit() {
    const selectedAI = this.elements.form.querySelector(
      'input[name="ai"]:checked'
    );
    const selectedFortunes = this.elements.form.querySelectorAll(
      'input[name="fortunes"]:checked'
    );
    const selectedPeriod = this.elements.form.querySelector(
      'input[name="period"]:checked'
    );

    if (!selectedAI || selectedFortunes.length === 0 || !selectedPeriod) {
      return;
    }

    this.elements.submitBtn.disabled = true;
    this.elements.loadingScreen.classList.remove('hidden');
    this.elements.contentsScreen.classList.add('hidden');
  },

  goToBirthInfo() {
    window.location.href = this.config.OPTION_BACK_URL;
  },

  initializePageState() {
    this.elements.loadingScreen.classList.add('hidden');
    this.elements.contentsScreen.classList.remove('hidden');

    this.elements.submitBtn.disabled = true;
  },
};

// 전역 함수 노출 (HTML onclick 이벤트용)
window.goToBirthInfo = () => FortuneOptionPage.goToBirthInfo();

window.addEventListener('pageshow', (event) => {
  // 앞으로 가기로 다시 돌아올 때
  if (event.persisted) {
    FortuneOptionPage.initializePageState();
  }
});

document.addEventListener('DOMContentLoaded', () => {
  FortuneOptionPage.init();
});
