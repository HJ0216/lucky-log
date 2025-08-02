// selection.html에서 데이터 읽기
const userData = JSON.parse(sessionStorage.getItem("userFormData"));

// 사용자 정보 표시 업데이트
if (userData) {
 // 성별 텍스트 변환
 const genderText = userData.gender === 'male' ? '남성' : '여성';

 // 달력 타입 텍스트 변환
 const calendarMap = {
   'solar': '양력',
   'lunar': '음력(평달)',
   'lunar_leap': '음력(윤달)'
 };
 const calendarText = calendarMap[userData.calendar] || '양력';

 const timeText = userData.time;

 // 년월일 포맷팅 (숫자를 2자리로)
 const formattedMonth = userData.month.padStart(2, '0');
 const formattedDay = userData.day.padStart(2, '0');

 // HTML 업데이트
 document.querySelector('.logo p').innerHTML =
   `${userData.city} ${genderText} ${calendarText}<br>${userData.year}년 ${formattedMonth}월 ${formattedDay}일${timeText ? ' ' + timeText : ''}`;
}

// 에러 애니메이션 적용 함수
function applyErrorAnimation(element, animationClass) {
  element.classList.add(animationClass);
  setTimeout(() => {
    element.classList.remove(animationClass);
  }, 1000);
}

// 에러 메시지 스택 표시 함수
function showErrorMessages(messages) {
  // 기존 에러 컨테이너 제거
  const existingContainer = document.querySelector(".error-container");
  if (existingContainer) {
    existingContainer.remove();
  }

  if (messages.length === 0) return;

  // 에러 컨테이너 생성
  const errorContainer = document.createElement("div");
  errorContainer.className = "error-container";

  // 각 에러 메시지를 스택으로 추가
  messages.forEach((message, index) => {
    const errorDiv = document.createElement("div");
    errorDiv.className = "error-message";
    errorDiv.textContent = message;

    // 순차적으로 나타나는 애니메이션 효과
    errorDiv.style.animationDelay = `${index * 0.1}s`;

    errorContainer.appendChild(errorDiv);
  });

  // 버튼 컨테이너 위에 삽입
  const btnContainer = document.querySelector(".retro-btn-container");
  btnContainer.parentNode.insertBefore(errorContainer, btnContainer);
}

// 에러 메시지 숨기기 함수
function hideErrorMessages() {
  const errorContainer = document.querySelector(".error-container");
  if (errorContainer) {
    errorContainer.remove();
  }
}

// 전체 폼 유효성 검사 함수
function validateForm() {
  const errors = [];

  // AI 선택 확인
  const ai = document.querySelector('input[name="ai"]:checked');
  if (!ai) {
    errors.push("🤖 AI를 선택해주세요!");
    const aiContainer = document.querySelector(".ai-cards");
    if (aiContainer) {
      applyErrorAnimation(aiContainer, "field-error-jump");
    }
  }

  // 운세 종류 선택 확인 (최소 1개 이상)
  const fortunes = document.querySelectorAll('input[name="fortune"]:checked');
  if (fortunes.length === 0) {
    errors.push("🍀 최소 하나의 운세를 선택해주세요!");
    const fortuneContainer = document.querySelector(".fortune-grid");
    if (fortuneContainer) {
      applyErrorAnimation(fortuneContainer, "field-error-jump");
    }
  }

  // 운세 주기 선택 확인
  const period = document.querySelector('input[name="period"]:checked');
  if (!period) {
    errors.push("📊 운세 주기를 선택해주세요!");
    const periodContainer = document.querySelector(".period-cards");
    if (periodContainer) {
      applyErrorAnimation(periodContainer, "field-error-jump");
    }
  }

  return {
    isValid: errors.length === 0,
    errors: errors,
  };
}

// 폼 제출 이벤트 핸들러
document.querySelector("form").addEventListener("submit", function (e) {
  e.preventDefault();

  const validation = validateForm();

  if (validation.isValid) {
    // 선택된 데이터 수집
    const ai = document.querySelector('input[name="ai"]:checked').value;
    const fortunes = Array.from(document.querySelectorAll('input[name="fortune"]:checked')).map(f => f.value);
    const period = document.querySelector('input[name="period"]:checked').value;

    // 선택 데이터 정리
    const selectionData = {
      ai: ai,
      fortunes: fortunes,
      period: period
    };

    // SessionStorage에 선택 데이터 저장
    sessionStorage.setItem("selectionData", JSON.stringify(selectionData));

    // 다음 페이지로 이동
    window.location.href = "/fortune.html";
  } else {
    showErrorMessages(validation.errors);
  }
});

// 모든 입력 변경 시 에러 메시지 숨기기
document.querySelectorAll('input[name="ai"], input[name="fortune"], input[name="period"]').forEach((input) => {
  input.addEventListener("change", hideErrorMessages);
});

// 운세 선택 시 시각적 피드백
document.querySelectorAll('input[name="fortune"]').forEach((checkbox) => {
  checkbox.addEventListener("change", function () {
    const selectedCount = document.querySelectorAll(
      'input[name="fortune"]:checked'
    ).length;
    
    if (selectedCount > 0) {
      // 선택된 개수를 표시할 수 있음
      console.log(`${selectedCount}개의 운세가 선택됨`);
    }
  });
});

// 애니메이션 효과를 위한 인터랙션
document
  .querySelectorAll('input[type="radio"], input[type="checkbox"]')
  .forEach((input) => {
    input.addEventListener("change", function () {
      // 선택 시 약간의 진동 효과
      const label = this.nextElementSibling;
      label.style.animation = "none";
      setTimeout(() => {
        label.style.animation = "";
      }, 10);
    });
  });
