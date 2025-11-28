'use strict';

const FortuneMyPage = {
  config: {
    ERROR_DURATION: 5000,
  },

  // DOM 요소 캐싱
  elements: {
      errorMessages: [],
  },

  init() {
    this.cacheElements();
    this.attachEvents();
    this.autoHideErrors();
  },

  cacheElements() {
    this.elements.errorMessages = document.querySelectorAll(
      '[data-error-message]'
    );
  },

  attachEvents() {},

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
