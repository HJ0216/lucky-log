 'use strict';

 const LoginPage = {
   config: {
     ANIMATION_DURATION: 300, // 0.3s
     ERROR_DURATION: 5000,
   },

   elements: {
     form: null,
     submitBtn: null,
     emailInput: null,
     passwordInput: null,
     allInputs: [],
     errorContainer: null,
     errorMessages: null,
   },

   init() {
     this.cacheElements();
     if (!this.validateRequiredElements()) return;
     this.attachEvents();
     this.autoHideErrors();
   },

   cacheElements() {
     this.elements.form = document.querySelector('form');
     this.elements.submitBtn = document.querySelector('[data-submit-btn]');
     this.elements.emailInput = document.querySelector('#email');
     this.elements.passwordInput = document.querySelector('#password');
     this.elements.allInputs = [
       this.elements.emailInput,
       this.elements.passwordInput,
     ];
     this.elements.errorContainer = document.querySelector(
       '[data-error-container]'
     );
     this.elements.errorMessages = document.querySelectorAll(
       '[data-error-message]'
     );
   },

   validateRequiredElements() {
     const required = [
       'form',
       'submitBtn',
       'emailInput',
       'passwordInput',
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
     this.elements.allInputs.forEach((input) => {
       input.addEventListener('change', () => this.hideErrors());
       input.addEventListener('input', () => this.hideErrors());
     });
   },

   // Error
   hideErrors() {
     const container = this.elements.errorContainer;
     if (!container) return;

     setTimeout(() => {
       container.classList.add('hidden');
     }, this.config.ANIMATION_DURATION);
   },

   autoHideErrors() {
     this.elements.errorMessages.forEach((message) => {
       if (!message.textContent.trim()) return;

       // fade-out 애니메이션이 끝난 후 display: none 처리
       setTimeout(() => {
         message.classList.add('hidden');
       }, this.config.ERROR_DURATION);
     });
   },
 };

 document.addEventListener('DOMContentLoaded', () => {
   LoginPage.init();
 });
