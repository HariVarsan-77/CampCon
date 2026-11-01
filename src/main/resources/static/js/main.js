// Campus Connect - Client interactions

document.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize Lucide icons
    if (window.lucide) {
        window.lucide.createIcons();
    }

    // 2. Password visibility toggles
    document.querySelectorAll('.password-toggle-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const input = btn.closest('.password-field').querySelector('input');
            if (input) {
                if (input.type === 'password') {
                    input.type = 'text';
                    btn.innerHTML = '<i data-lucide="eye-off"></i>';
                } else {
                    input.type = 'password';
                    btn.innerHTML = '<i data-lucide="eye"></i>';
                }
                if (window.lucide) window.lucide.createIcons();
            }
        });
    });

    // 3. Image Upload Preview & Reset
    const fileInput = document.getElementById('imageFileInput');
    const previewContainer = document.getElementById('imagePreviewContainer');
    const previewImg = document.getElementById('imagePreviewImg');
    const removeBtn = document.getElementById('removeImageBtn');
    const uploadPrompt = document.getElementById('uploadPrompt');

    if (fileInput && previewContainer && previewImg) {
        fileInput.addEventListener('change', function (e) {
            const file = this.files[0];
            if (file) {
                // Check allowed file extensions
                const validTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/jpg'];
                if (!validTypes.includes(file.type)) {
                    alert('Invalid file format. Please select a JPG, JPEG, PNG, or WEBP image.');
                    this.value = '';
                    return;
                }

                // Check size (10MB)
                if (file.size > 10 * 1024 * 1024) {
                    alert('File exceeds 10MB limit. Please choose a smaller file.');
                    this.value = '';
                    return;
                }

                const reader = new FileReader();
                reader.onload = function (event) {
                    previewImg.src = event.target.result;
                    previewContainer.style.display = 'block';
                    if (uploadPrompt) uploadPrompt.style.display = 'none';
                };
                reader.readAsDataURL(file);
            }
        });

        if (removeBtn) {
            removeBtn.addEventListener('click', function (e) {
                e.preventDefault();
                fileInput.value = '';
                previewImg.src = '';
                previewContainer.style.display = 'none';
                if (uploadPrompt) uploadPrompt.style.display = 'block';
            });
        }
    }

    // 4. Test account quick-fill helper
    window.fillLogin = function(email, password, roleTab) {
        const emailInput = document.getElementById('email');
        const passInput = document.getElementById('password');
        if (emailInput && passInput) {
            emailInput.value = email;
            passInput.value = password;
        }
        if (roleTab) {
            switchLoginTab(roleTab);
        }
    };

    // 5. Auth tabs switcher
    window.switchLoginTab = function(role) {
        document.querySelectorAll('.auth-tab-btn').forEach(btn => btn.classList.remove('active'));
        const activeBtn = document.getElementById(role + 'TabBtn');
        if (activeBtn) activeBtn.classList.add('active');

        const roleBadge = document.getElementById('currentRoleLabel');
        if (roleBadge) {
            roleBadge.textContent = role === 'management' ? 'Management Staff Portal' : 'Student Grievance Portal';
        }
    };

    // 6. Generic Modal Open/Close helpers
    window.openModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('open');
            document.body.style.overflow = 'hidden';
            if (window.lucide) window.lucide.createIcons();
        }
    };

    window.closeModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('open');
            document.body.style.overflow = '';
        }
    };

    // Close modal on background click
    document.querySelectorAll('.modal-backdrop').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('open');
                document.body.style.overflow = '';
            }
        });
    });

    // 7. Auto-dismiss toasts after 5 seconds
    document.querySelectorAll('.toast').forEach(toast => {
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            toast.style.transition = 'all 0.4s ease';
            setTimeout(() => toast.remove(), 400);
        }, 5000);
    });

    // 8. Mobile Sidebar Toggle
    const mobileMenuBtn = document.getElementById('mobileMenuToggle');
    const sidebar = document.querySelector('.sidebar');
    if (mobileMenuBtn && sidebar) {
        mobileMenuBtn.addEventListener('click', () => {
            sidebar.classList.toggle('mobile-open');
        });
    }
});

