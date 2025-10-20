'use strict';

const FortuneResultPage = {
  config: {
    INDEX_URL: '/',
  },

  messages: {
    copySuccess: '운세 결과가 복사되었습니다! 📋',
    copyFailed: '복사에 실패했습니다. 😅',
    shareSuccess: '공유 링크가 복사되었습니다! 🔗',
    shareFailed: '공유에 실패했습니다. 😅',
    saveSuccess: '운세가 저장되었습니다! 📂',
    saveFailed: '저장에 실패했습니다. 😅',
    loginRequired: '로그인 페이지로 이동합니다! 🔐',
  },

  // DOM 요소 캐싱
  elements: {
    resultScreen: null,
    retryBtn: null,
    copyBtn: null,
    shareBtn: null,
    saveBtn: null,
  },

  init() {
    this.cacheElements();
    if (!this.validateRequiredElements()) return;
    this.attachEvents();
  },

  cacheElements() {
    this.elements.resultScreen = document.querySelector('.result-screen');
    this.elements.retryBtn = document.querySelector('[data-retry-btn]');
    this.elements.copyBtn = document.querySelector('[data-copy-btn]');
    this.elements.shareBtn = document.querySelector('[data-share-btn]');
    this.elements.saveBtn = document.querySelector('[data-save-btn]');
  },

  validateRequiredElements() {
    const required = [
      'resultScreen',
      'retryBtn',
      'copyBtn',
      'shareBtn',
      'saveBtn',
    ];

    const missing = required.filter((key) => !this.elements[key]);
    if (missing.length > 0) {
      const message = `Missing required elements: ${missing.join(', ')}`;

      console.error(message);
      return false;
    }

    return true;
  },

  attachEvents() {
    this.elements.retryBtn.addEventListener('click', () => {
      window.location.href = this.config.INDEX_URL;
    });
    this.elements.copyBtn.addEventListener('click', () => {
      // TODO: 구현 예정
    });
    this.elements.shareBtn.addEventListener('click', () => {
      // TODO: 구현 예정
    });
    this.elements.saveBtn.addEventListener('click', () => {
      // TODO: 구현 예정
    });
  },
};

document.addEventListener('DOMContentLoaded', () => {
  FortuneResultPage.init();
});
