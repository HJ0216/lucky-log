'use strict';

const Toast = {
  config: {
    duration: 3000,
    position: 'top-right',
    maxToasts: 3,
    animationDuration: 300,
  },

  container: null,
  activeToasts: 0,

  init(customConfig = {}) {
    this.config = { ...this.config, ...customConfig };
    this.createContainer();
  },

  createContainer() {
    if (this.container) return;

    const container = document.createElement('div');
    container.className = `toast-container ${this.config.position}`;

    document.body.appendChild(container);
    this.container = container;
  },

  /**
   * 토스트 표시
   * @param {string} type - 토스트 타입 (success, error, warning, info)
   * @param {string} title - 제목
   * @param {string} message - 메시지
   * @param {number} duration - 표시 시간 (선택, 기본값: config.duration)
   */
  show(type = 'info', title = '', message = '', duration = null) {
    if (!title?.trim() && !message?.trim()) {
      console.warn('[Toast] title 또는 message 중 하나는 필수입니다.');
      return;
    }

    if (!this.container) {
      this.init();
    }

    if (this.activeToasts >= this.config.maxToasts) {
      const oldestToast = this.container.querySelector('.toast');
      if (oldestToast) {
        this.removeToast(oldestToast);
      }
    }

    const displayDuration = duration || this.config.duration;
    const toast = this.createToastElement(type, title, message);

    this.container.appendChild(toast);
    this.activeToasts++;

    requestAnimationFrame(() => {
      toast.classList.add('show');
    });

    setTimeout(() => {
      this.removeToast(toast);
    }, displayDuration);
  },

  createToastElement(type, title, message) {
    const toast = document.createElement('div');

    // 위치에 따른 초기 transform 설정
    const isRightPosition = this.config.position.includes('right');
    const slideClass = isRightPosition
      ? 'slide-right-enter'
      : 'slide-left-enter';

    toast.className = `toast toast-${type} ${slideClass}`;

    const content = document.createElement('div');
    content.className = 'toast-content';

    const body = document.createElement('div');
    body.className = 'toast-body';

    if (title) {
      const titleEl = document.createElement('div');
      titleEl.className = 'toast-title';
      titleEl.textContent = title;
      body.appendChild(titleEl);
    }

    if (message) {
      const messageEl = document.createElement('div');
      messageEl.className = 'toast-message';
      messageEl.textContent = message;
      body.appendChild(messageEl);
    }

    toast.addEventListener('click', () => {
      this.removeToast(toast);
    });

    return toast;
  },

  removeToast(toast) {
    // 같은 toast가 2번 count되는 경우 방지
    if (!toast || !toast.parentNode || toast.classList.contains('removing'))
      return;

    // 제거 중임을 표시 (중복 실행 방지용 플래그)
    toast.classList.add('removing');

    const isRightPosition = this.config.position.includes('right');
    const exitClass = isRightPosition ? 'slide-right-exit' : 'slide-left-exit';

    toast.classList.remove('show');
    toast.classList.add(exitClass);

    setTimeout(() => {
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast);
        this.activeToasts--;
      }
    }, this.config.animationDuration);
  },

  success(title, message, duration) {
    this.show('success', title, message, duration);
  },

  error(title, message, duration) {
    this.show('error', title, message, duration);
  },

  warning(title, message, duration) {
    this.show('warning', title, message, duration);
  },

  info(title, message, duration) {
    this.show('info', title, message, duration);
  },

  clearAll() {
    if (!this.container) return;

    const toasts = this.container.querySelectorAll('.toast');
    toasts.forEach((toast) => this.removeToast(toast));
  },
};

// 전역 객체로 노출
window.toast = Toast;

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    Toast.init();
  });
} else {
  Toast.init();
}
