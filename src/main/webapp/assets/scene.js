/**
 * 动态背景：上浮气泡 + 人物眨眼。
 * 低性能/减弱动画偏好下自动关闭。
 */
(function () {
    var reduced = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    /* ---------- 气泡 ---------- */
    var canvas = document.getElementById('sceneBubbles');
    if (canvas && !reduced) {
        var ctx = canvas.getContext('2d');
        var dpr = Math.min(window.devicePixelRatio || 1, 2);
        var bubbles = [];
        var width = 0;
        var height = 0;
        var running = true;

        function resize() {
            width = window.innerWidth;
            height = window.innerHeight;
            canvas.width = width * dpr;
            canvas.height = height * dpr;
            canvas.style.width = width + 'px';
            canvas.style.height = height + 'px';
            ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
        }

        function makeBubble(randomY) {
            var r = 0.8 + Math.random() * 3.2;
            return {
                x: Math.random() * width,
                y: randomY ? Math.random() * height : height + 10,
                r: r,
                speed: 10 + Math.random() * 26 + r * 3,
                drift: (Math.random() - 0.5) * 12,
                phase: Math.random() * Math.PI * 2,
                alpha: 0.12 + Math.random() * 0.34
            };
        }

        function initBubbles() {
            var count = Math.min(72, Math.max(24, Math.round(width / 26)));
            bubbles = [];
            for (var i = 0; i < count; i++) {
                bubbles.push(makeBubble(true));
            }
        }

        function draw(now) {
            if (!running) {
                return;
            }
            var dt = Math.min(48, now - (draw.last || now));
            draw.last = now;
            ctx.clearRect(0, 0, width, height);
            for (var i = 0; i < bubbles.length; i++) {
                var b = bubbles[i];
                b.y -= (b.speed * dt) / 1000;
                b.phase += dt / 900;
                var x = b.x + Math.sin(b.phase) * b.drift;
                if (b.y < -12) {
                    bubbles[i] = makeBubble(false);
                    continue;
                }
                ctx.beginPath();
                ctx.arc(x, b.y, b.r, 0, Math.PI * 2);
                ctx.fillStyle = 'rgba(168, 226, 255, ' + b.alpha * 0.5 + ')';
                ctx.fill();
                ctx.beginPath();
                ctx.arc(x - b.r * 0.3, b.y - b.r * 0.3, Math.max(0.4, b.r * 0.32), 0, Math.PI * 2);
                ctx.fillStyle = 'rgba(255, 255, 255, ' + Math.min(0.6, b.alpha + 0.2) + ')';
                ctx.fill();
            }
            requestAnimationFrame(draw);
        }

        resize();
        initBubbles();
        requestAnimationFrame(draw);

        window.addEventListener('resize', function () {
            resize();
            initBubbles();
        });

        document.addEventListener('visibilitychange', function () {
            if (document.hidden) {
                running = false;
            } else if (!running) {
                running = true;
                draw.last = 0;
                requestAnimationFrame(draw);
            }
        });
    }

})();
