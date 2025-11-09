'use strict';

const FortuneOptionPage = {
  config: {
    ERROR_DURATION: 5000,
    OPTION_BACK_URL: '/fortune/option/back',
    OPTION_DISABLED_TOOLTIP: '준비 중인 기능입니다.',
  },

  // DOM 요소 캐싱
  elements: {
    loadingScreen: null,
    contentsScreen: null,
    backBtn: null,
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
    this.elements.backBtn = document.querySelector('[data-back-btn]');
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
    const required = [
      'loadingScreen',
      'contentsScreen',
      'form',
      'submitBtn',
      'backBtn',
    ];

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
    this.elements.form.addEventListener('submit', () => this.handleSubmit());
    this.elements.backBtn.addEventListener('click', () => {
      window.location.href = this.config.OPTION_BACK_URL;
    });
  },

  // error messages
  autoHideErrors() {
    this.elements.errorMessages.forEach((message) => {
      // 메시지에 내용이 있을 때만 타이머 작동
      if (!message.textContent.trim()) return;

      setTimeout(() => {
        message.classList.add('hidden');
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

  initializePageState() {
    this.elements.loadingScreen.classList.add('hidden');
    this.elements.contentsScreen.classList.remove('hidden');

    this.elements.submitBtn.disabled = false;
  },
};

document.addEventListener('DOMContentLoaded', () => {
  FortuneOptionPage.init();
});

window.addEventListener('pageshow', (event) => {
  if (event.persisted) {
    FortuneOptionPage.initializePageState();
  }
});
