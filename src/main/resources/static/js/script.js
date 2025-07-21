async function fetchUsedUserData() {
    const url = '/api/v1/users';
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`API 요청 실패: ${response.status}`);
        }
        const users = await response.json(); // 결과: [{email: "...", nickname: "..."}, ...]

        const usedNicknames = users.map(user => user.nickname.toLowerCase());
        const usedEmails = users.map(user => user.email.toLowerCase());

        return { usedNicknames, usedEmails };

    } catch (error) {
        console.error('사용자 목록을 가져오는 데 실패했습니다:', error);
        return { usedNicknames: [], usedEmails: [] };
    }
}

let currentUsedNicknames = [];
let currentUsedEmails = [];

function setupNicknameValidation(usedNicknames) {
    const nicknameInput = document.getElementById('nickname-input');
    const validationIcon = document.getElementById('nickname-validation');

    if (!nicknameInput) return; // 해당 요소가 없으면 함수 종료

    nicknameInput.addEventListener('blur', () => {
        const nickname = nicknameInput.value.trim();
        if (nickname === '') {
            validationIcon.textContent = '';
            return;
        }

        const isInvalid = nickname.length < 2 || currentUsedNicknames.includes(nickname.toLowerCase());
        validationIcon.textContent = isInvalid ? '❌' : '✅';
    });

    nicknameInput.addEventListener('input', () => {
        validationIcon.textContent = '';
    });
}

function setupEmailValidation(usedEmails) {
    const emailInput = document.getElementById('signup-email');
    const validationIcon = document.getElementById('email-validation');

    if (!emailInput) return;

    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

    emailInput.addEventListener('blur', () => {
        const email = emailInput.value.trim();
        if (email === '') {
            validationIcon.textContent = '';
            return;
        }

        const isInvalid = !emailRegex.test(email) || email.length > 50 || currentUsedEmails.includes(email.toLowerCase());
        validationIcon.textContent = isInvalid ? '❌' : '✅';
    });

    emailInput.addEventListener('input', () => {
        validationIcon.textContent = '';
    });
}

function setupPasswordValidation() {
    const passwordInput = document.getElementById('signup-password');
    const validationIcon = document.getElementById('password-validation');

    if (!passwordInput) return;

    passwordInput.addEventListener('blur', () => {
        const password = passwordInput.value.trim();
        if (password === '') {
            validationIcon.textContent = '';
            return;
        }

        const isInvalid = password.length < 8 || password.length > 20;
        validationIcon.textContent = isInvalid ? '❌' : '✅';
    });

    passwordInput.addEventListener('input', () => {
        validationIcon.textContent = '';
    });
}

function setupPasswordConfirmValidation() {
    const passwordInput = document.getElementById('signup-password');
    const passwordConfirmInput = document.getElementById('password-confirm');
    const validationIcon = document.getElementById('password-confirm-validation');

    if (!passwordConfirmInput || !passwordInput) return;

    passwordConfirmInput.addEventListener('blur', () => {
        const originalPassword = passwordInput.value;
        const confirmPassword = passwordConfirmInput.value;

        if (confirmPassword === '') {
            validationIcon.textContent = '';
            return;
        }

        validationIcon.textContent = (originalPassword === confirmPassword) ? '✅' : '❌';
    });

    passwordConfirmInput.addEventListener('input', () => {
        validationIcon.textContent = '';
    });
}

async function refreshUserData() {
    const { usedNicknames, usedEmails } = await fetchUsedUserData();
    currentUsedNicknames = usedNicknames;
    currentUsedEmails = usedEmails;
}

function setupSignupForm() {
    const signupForm = document.getElementById('signup-form');
    if (!signupForm) return;

    signupForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // 폼 기본 제출 동작 방지

        // 모든 유효성 검사 아이콘이 '✅'인지 확인
        const validationIcons = signupForm.querySelectorAll('.validation-icon');
        const isAllValid = Array.from(validationIcons).every(icon => icon.textContent === '✅');

        if (!isAllValid) {
            alert('입력 정보를 다시 확인해주세요. 모든 항목에 ✅ 표시가 필요합니다.');
            return;
        }

        // 서버로 전송할 데이터 구성
        const userData = {
            nickname: document.getElementById('nickname-input').value,
            email: document.getElementById('signup-email').value,
            password: document.getElementById('signup-password').value,
        };

        // 서버에 회원가입 요청
        try {
            const response = await fetch('/api/v1/users', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userData),
            });

            if (response.status === 201) { // 201 Created
                alert('회원가입에 성공했습니다! 로그인 페이지로 이동합니다.');
                await refreshUserData();
                clearSignupForm();
                document.getElementById('login-tab').checked = true; // 로그인 탭으로 전환
            } else {
                const errorData = await response.json();
                alert(`회원가입 실패: ${errorData.message}`);
            }
        } catch (error) {
            console.error('회원가입 요청 중 네트워크 오류:', error);
            alert('서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        }
    });
}

function clearSignupForm() {
    const signupForm = document.getElementById('signup-form');
    if (!signupForm) return;

    // 1. form.reset()을 사용해 모든 input 필드의 값을 초기화합니다.
    signupForm.reset();

    // 2. 모든 유효성 검사 아이콘의 텍스트를 지웁니다.
    const validationIcons = signupForm.querySelectorAll('.validation-icon');
    validationIcons.forEach(icon => {
        icon.textContent = '';
    });
}

document.addEventListener('DOMContentLoaded', async () => {
    // 1. 필요한 데이터를 서버에서 한 번에 가져옵니다.
    const { usedNicknames, usedEmails } = await fetchUsedUserData();

    currentUsedNicknames = usedNicknames;
    currentUsedEmails = usedEmails;

    // 2. 각 입력 필드에 대한 유효성 검사 로직을 설정합니다.
    setupNicknameValidation(usedNicknames);
    setupEmailValidation(usedEmails);
    setupPasswordValidation();
    setupPasswordConfirmValidation();

    // 3. 회원가입 폼 제출 로직을 설정합니다.
    setupSignupForm();
});