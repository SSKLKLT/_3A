/**
 * 大肥鱼娘桌宠：可拖动、会自己乱逛、点一下就找大模型聊天。
 */
(function () {
    var pet = document.getElementById('pet');
    if (!pet) {
        return;
    }

    var bubble = document.getElementById('petBubble');
    var img = document.getElementById('petImg');
    var ctx = pet.getAttribute('data-ctx') || '';

    var QUESTIONS = [
        '今天推荐我玩哪款 3A？',
        '打 Boss 老是死怎么办？',
        '用一句话夸夸我',
        '3A 游戏开荒最重要的是什么？',
        '讲个关于肥鱼的冷笑话',
        '我现在该去签到还是去发帖？'
    ];
    var NETWORK_TIP = '咕噜…我的鱼线好像断了，稍后再聊？🐟';

    var dragging = false;
    var moved = false;
    var grabX = 0;
    var grabY = 0;
    var wander = true;
    var bubbleTimer = null;
    var chatBusy = false;
    var MOVE_TOLERANCE = 8;

    pet.setAttribute('role', 'button');
    pet.setAttribute('tabindex', '0');

    function clamp(value, min, max) {
        return Math.max(min, Math.min(max, value));
    }

    function moveTo(x, y) {
        var w = pet.offsetWidth || 124;
        var h = pet.offsetHeight || 146;
        pet.style.left = clamp(x, 4, window.innerWidth - w - 4) + 'px';
        pet.style.top = clamp(y, 56, window.innerHeight - h - 4) + 'px';
    }

    function say(text, duration) {
        bubble.textContent = text;
        bubble.classList.add('show');
        img.classList.add('talking');
        clearTimeout(bubbleTimer);
        bubbleTimer = setTimeout(function () {
            bubble.classList.remove('show');
            img.classList.remove('talking');
        }, duration || 8000);
    }

    function pick(list) {
        return list[Math.floor(Math.random() * list.length)];
    }

    function talk() {
        if (chatBusy) {
            return;
        }
        chatBusy = true;
        say('咕噜咕噜…让我想想 💭', 30000);

        fetch(ctx + '/api/pet-chat?q=' + encodeURIComponent(pick(QUESTIONS)), {
            headers: {'Accept': 'text/plain'}
        }).then(function (res) {
            if (!res.ok) {
                throw new Error('HTTP ' + res.status);
            }
            return res.text();
        }).then(function (text) {
            var clean = (text || '').replace(/<[^>]*>/g, '').trim();
            say(clean ? clean.slice(0, 140) : NETWORK_TIP);
            chatBusy = false;
        }).catch(function (err) {
            var status = (err && err.message ? err.message : '').indexOf('HTTP') === 0
                ? err.message
                : '';
            say(status ? '咕噜…接口不通（' + status + '）' : NETWORK_TIP);
            chatBusy = false;
        });
    }

    // 初始位置：右下角角落
    moveTo(window.innerWidth - 132, window.innerHeight - 178);

    pet.addEventListener('pointerdown', function (event) {
        if (event.button !== undefined && event.button !== 0) {
            return;
        }
        dragging = true;
        moved = false;
        var rect = pet.getBoundingClientRect();
        grabX = event.clientX - rect.left;
        grabY = event.clientY - rect.top;
        if (pet.setPointerCapture) {
            try {
                pet.setPointerCapture(event.pointerId);
            } catch (e) {
                // 忽略：部分浏览器不支持
            }
        }
        pet.classList.add('dragging');
        event.preventDefault();
    });

    pet.addEventListener('pointermove', function (event) {
        if (!dragging) {
            return;
        }
        var rect = pet.getBoundingClientRect();
        var nextX = event.clientX - grabX;
        var nextY = event.clientY - grabY;
        if (Math.abs(nextX - rect.left) > MOVE_TOLERANCE || Math.abs(nextY - rect.top) > MOVE_TOLERANCE) {
            moved = true;
        }
        if (moved) {
            moveTo(nextX, nextY);
        }
    });

    pet.addEventListener('pointerup', function (event) {
        if (!dragging) {
            return;
        }
        dragging = false;
        pet.classList.remove('dragging');
        if (pet.releasePointerCapture) {
            try {
                pet.releasePointerCapture(event.pointerId);
            } catch (e) {
                // 忽略
            }
        }
        if (moved) {
            say('哎哟，被你拎来拎去～', 4000);
        } else {
            talk();
        }
    });

    pet.addEventListener('pointercancel', function () {
        dragging = false;
        pet.classList.remove('dragging');
    });

    // 键盘回车也能聊天
    pet.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            talk();
        }
    });

    // 双击切换自动乱逛
    pet.addEventListener('dblclick', function () {
        wander = !wander;
        say(wander ? '我又开始游啦～' : '我趴一会儿，别吵我～', 4000);
    });

    // 自动乱逛
    setInterval(function () {
        if (!wander || dragging || document.hidden) {
            return;
        }
        moveTo(pet.offsetLeft + (Math.random() * 160 - 80), pet.offsetTop + (Math.random() * 90 - 45));
    }, 5200);

    // 进页面打个招呼
    setTimeout(function () {
        say('嘿嘿，我是大肥鱼娘～点我可以聊天哦 🎏');
    }, 1200);
})();
