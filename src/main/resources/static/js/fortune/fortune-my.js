'use strict';

const FortuneOptionPage = {
  config: { ERROR_DURATION: 5000 },

  // DOM 요소 캐싱
  elements: {},

  init() {
    this.cacheElements();
    if (!this.validateRequiredElements()) return;
    this.attachEvents();
    this.autoHideErrors();
  },

  cacheElements() {},

  validateRequiredElements() {
    const required = [];

    const missing = required.filter((key) => !this.elements[key]);
    if (missing.length > 0) {
      const message = `Missing required elements: ${missing.join(', ')}`;

      console.error(message);
      return false;
    }

    return true;
  },

  attachEvents() {},

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
};

document.addEventListener('DOMContentLoaded', () => {
  FortuneMyPage.init();
});
