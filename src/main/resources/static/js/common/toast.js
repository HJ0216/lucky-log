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
    container.id = 'toast-container';
    container.className = 'toast-container';

    const positions = {
      'top-right': 'top: 20px; right: 20px;',
      'top-left': 'top: 20px; left: 20px;',
      'bottom-right': 'bottom: 20px; right: 20px;',
      'bottom-left': 'bottom: 20px; left: 20px;',
    };

    container.style.cssText = `
      position: fixed;
      ${positions[this.config.position] || positions['top-right']}
      z-index: 9999;
      pointer-events: none;
      display: flex;
      flex-direction: column;
      gap: 10px;
    `;

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
      toast.style.transform = 'translateX(0)';
      toast.style.opacity = '1';
    });

    setTimeout(() => {
      this.removeToast(toast);
    }, displayDuration);
  },

  createToastElement(type, title, message) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    // 위치에 따른 초기 transform 설정
    const isRightPosition = this.config.position.includes('right');
    const initialTransform = isRightPosition
      ? 'translateX(400px)'
      : 'translateX(-400px)';

    toast.style.cssText = `
      background: ${this.getBackground(type)};
      color: ${type === 'warning' ? '#333' : '#fff'};
      padding: 16px 24px;
      border-radius: 12px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.3);
      transform: ${initialTransform};
      opacity: 0;
      transition: all 0.3s ease;
      pointer-events: auto;
      max-width: 350px;
      min-width: 250px;
      font-size: 0.8rem;
      cursor: pointer;
    `;

    toast.innerHTML = `
      <div style="display: flex; align-items: start; gap: 12px;">
        <div style="flex: 1;">
          ${
            title
              ? `<div style="font-weight: bold; margin-bottom: 4px;">${title}</div>`
              : ''
          }
          ${message ? `<div>${message}</div>` : ''}
        </div>
      </div>
    `;

    toast.addEventListener('click', () => {
      this.removeToast(toast);
    });

    return toast;
  },

  removeToast(toast) {
    // 같은 toast가 2번 count되는 경우 방지
    if (!toast || !toast.parentNode || toast.style.opacity === '0') return;

    const isRightPosition = this.config.position.includes('right');
    const exitTransform = isRightPosition
      ? 'translateX(400px)'
      : 'translateX(-400px)';

    toast.style.transform = exitTransform;
    toast.style.opacity = '0';

    setTimeout(() => {
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast);
        this.activeToasts--;
      }
    }, this.config.animationDuration);
  },

  getBackground(type) {
    const backgrounds = {
      success: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      error: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
      warning: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
      info: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    };
    return backgrounds[type] || backgrounds.info;
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
