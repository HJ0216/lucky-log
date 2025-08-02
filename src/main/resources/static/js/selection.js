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

// 폼 검증
document.querySelector("form").addEventListener("submit", function (e) {
  e.preventDefault();

  // 필수 필드 검증
  const ai = document.querySelector('input[name="ai"]:checked');
  const fortunes = document.querySelectorAll('input[name="fortune"]:checked');
  const period = document.querySelector('input[name="period"]:checked');

  let isValid = true;
  let errorMessage = "";

  if (!ai) {
    isValid = false;
    errorMessage += "AI를 선택해주세요!\n";
    // AI 카드들에 에러 효과
    document.querySelectorAll(".ai-option label").forEach((label) => {
      label.style.borderColor = "#e74c3c";
      setTimeout(() => (label.style.borderColor = ""), 2000);
    });
  }

  if (fortunes.length === 0) {
    isValid = false;
    errorMessage += "최소 하나의 운세를 선택해주세요!\n";
    // 운세 카드들에 에러 효과
    document.querySelectorAll(".fortune-option label").forEach((label) => {
      label.style.borderColor = "#e74c3c";
      setTimeout(() => (label.style.borderColor = ""), 2000);
    });
  }

  if (!period) {
    isValid = false;
    errorMessage += "기간을 선택해주세요!\n";
    // 기간 카드들에 에러 효과
    document.querySelectorAll(".period-option label").forEach((label) => {
      label.style.borderColor = "#e74c3c";
      setTimeout(() => (label.style.borderColor = ""), 2000);
    });
  }

  if (isValid) {
    // 선택된 값들 정리
    const selectedFortunes = Array.from(fortunes).map((f) => f.value);

    alert(
      `🔮 설정 완료!\n\nAI: ${ai.value.toUpperCase()}\n운세: ${
        selectedFortunes.length
      }개 선택\n기간: ${period.value}\n\n운세 분석을 시작합니다! ✨`
    );

    // 여기에 다음 단계 또는 결과 페이지로 이동하는 로직 추가
    // window.location.href = 'result.html';
  } else {
    alert(errorMessage.trim());
  }
});

// 운세 선택 시 시각적 피드백
document.querySelectorAll('input[name="fortune"]').forEach((checkbox) => {
  checkbox.addEventListener("change", function () {
    const selectedCount = document.querySelectorAll(
      'input[name="fortune"]:checked'
    ).length;
    const label =
      document.querySelector('label[for="fortune"]') ||
      document.querySelector(".field-label");

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
