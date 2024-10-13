---
title: 捐赠

layout: home
hero:
  name: 捐赠
  text:
  tagline: 给开发者买杯奶茶吧
  image:
  actions:
    - theme: brand
      text: 回到首页
      link: /zh-CN/

features:
  - title: 微信
    icon: { src: /icons/wechat.svg }
    details: <img alt="微信赞赏码" src=/Third-Person/donate/wechat.jpg style="width:100%;image-rendering:pixelated" />
    link:

  - title: 支付宝
    icon: { src: /icons/alipay.svg }
    details: <img alt="支付宝" name=alipay src="/Third-Person/donate/alipay.jpg" style="width:100%;image-rendering:pixelated" />
    link: https://qr.alipay.com/fkx13526acsazyxpidsxj0e

  - title: 爱发电
    icon:
        src: /icons/afdian.png
    details: <img alt="爱发电" name=afdian src="/Third-Person/donate/afdian.jpg" style="width:100%;image-rendering:pixelated"></img>
    link: https://afdian.com/a/Leawind

  - title: Buy me a coffee
    icon: { src: /icons/bmc.svg }
    details: <img alt="Buy leawind a coffee" name=bmc src="/Third-Person/donate/bmc.png" style="width:100%;image-rendering:pixelated"></img>
    link: https://www.buymeacoffee.com/leawind

  # - title: Patreon
  #   icon:
  #       dark: /icons/patreon-dark.svg
  #       light: /icons/patreon-light.svg
  #   details: 暂无
  #   link:
---

<br>
<h1 style="text-align:center">你来助我！</h1>
<br>

<img src=/donate/IHY-86.jpg style="width: 80%;margin:auto;">

<script>
(async () => {
    class Bimg {
        w;
        h;
        code;
        get size() { return this.w * this.h; }

        putToCvs(cvs) {
            const ctx = wrapCvs(cvs, this.w, this.h);
            const imageData = ctx.getImageData(0, 0, this.w, this.h);
            imageData.data.set(bools2rgbas(bytes2bools(code2bytes(this.code)).slice(0, this.size)));
            ctx.putImageData(imageData, 0, 0);
        }

        putToImg(img) {
            img.style.imageRendering = 'pixelated';
            img.style.width = '100%';
            const cvs = document.createElement('canvas');
            this.putToCvs(cvs);

            img.src = cvs.toDataURL();
        }

        toString() {
            return `Bimg.of(${JSON.stringify(this)})`;
        }

        static of({ w, h = 0, code }) {
            h = h < 1 ? w : h;
            const bimg = new Bimg();
            bimg.w = w;
            bimg.h = h;
            bimg.code = code;
            return bimg;
        }
        static async ofUrl(url) {
            const img = await url2img(url);
            return this.of({
                w: img.naturalWidth,
                h: img.naturalHeight,
                code: bytes2code(bools2bytes(rgbas2bools(img2rgbas(img)))),
            });
        }
    }


    async function url2img(url) {
        const img = document.createElement('img');
        img.src = url;
        await new Promise(resolve => img.addEventListener('load', resolve));
        return img;
    }

    function img2rgbas(img) {
        const cvs = document.createElement('canvas');
        const ctx = wrapCvs(cvs, img.naturalWidth, img.naturalHeight);
        ctx.drawImage(img, 0, 0);
        return ctx.getImageData(0, 0, cvs.width, cvs.height).data;
    }


    function rgbas2bools(rgbas) {
        const bools = new Array(rgbas.length / 4);
        for (let I = 0; I < bools.length; I++) {
            const r = rgbas[I * 4 + 0];
            const g = rgbas[I * 4 + 1];
            const b = rgbas[I * 4 + 2];
            bools[I] = (r + g + b) / 3 > 128;
        }
        return bools;
    }


    function bools2bytes(bools) {
        const bytes = new Uint8ClampedArray(Math.ceil(bools.length / 8.0));
        for (let I = 0; I < bytes.length; I++) {
            let byte = 0;
            for (let j = 0; j < 8; j++)
                byte |= bools[(I * 8 + j)] << j;
            bytes[I] = byte;
        }
        return bytes;
    }

    function bytes2code(bytes) {
        return btoa(String.fromCharCode.apply(null, bytes));
    }

    function code2bytes(code) {
        let str = atob(code);
        let bytes = new Uint8ClampedArray(str.length);
        for (let i = 0; i < str.length; i++)
            bytes[i] = str.charCodeAt(i);
        return bytes;
    }


    function bytes2bools(bytes) {
        const bools = new Array(bytes.length);
        for (let I = 0; I < bytes.length; I++) {
            for (let j = 0; j < 8; j++)
                bools[(I * 8 + j)] = !!(bytes[I] & (1 << j));
        }
        return bools;
    }

    function bools2rgbas(bools) {
        const rgbas = new Uint8ClampedArray(bools.length * 4);
        for (let I = 0; I < bools.length; I++) {
            const value = bools[I] ? 255 : 0;
            rgbas[I * 4 + 0] = value;
            rgbas[I * 4 + 1] = value;
            rgbas[I * 4 + 2] = value;
            rgbas[I * 4 + 3] = 255;
        }
        return rgbas;
    }

    function wrapCvs(cvs, w, h) {
        cvs.style.imageRendering = 'pixelated';
        cvs.style.width = '100%';
        cvs.width = w;
        cvs.height = h;
        return cvs.getContext('2d');
    }

    {
        if (!globalThis.window) return;
        while (document.readyState !== 'complete') await sleep();

        // console.debug((await Bimg.ofUrl('/Third-Person/donate/alipay.jpg')).toString());
        // console.debug((await Bimg.ofUrl('/Third-Person/donate/afdian.jpg')).toString());
        // console.debug((await Bimg.ofUrl('/Third-Person/donate/bmc.jpg')).toString());

        const bimg_alipay = Bimg.of({ w: 37, code: "////////////D1hiKOB9/+T1vSjtwaIXZdtW9KIuTore17lG3wOqqgr4f1qw/y+YbCj4/ZyrBvzKBw/7f2YOgve3Io42f05u0+ALsKA8+fCPvF1PIMGN/N+NDiB+qYJ087dqv9r0qVzbO19OyYnwKwzWvn3xTIFdrxNaDOj/vfIcPeA0qdL3hQd38KJUjqBfVGQr4ovSsEB/36tsNg/ohaT3////////////AQ==" });
        const bimg_afdian = Bimg.of({w:31,h:31,code:"/////4D+P2DfdNe3aJAtWlQEFS0qKYr2JSh9A6qqgP9xy/8uGBbs5fI/PSjBDHqoibrv+Dwb/0O00nOmy8ChXYPvmgp0cOT/b/0mwgx+L8j4zao84P8XdMcDuouifVPf5aIaBWzR64m4aJ202HeNAQ+o+Gv/////AQ=="})
        const bimg_bmc = Bimg.of({ "w": 33, "h": 33, "code": "//////////8PaC8C3hdp9b3o1Ct6URhW9KKxteh9m3DfA6qqgP8Hof8vsTxaPorZhj5CsDr94LG3+7EjLv+bUVPzJ0pT6IcqBTBfsqovv5v17L3qQ1/+Kw/K9S2FBOr/8cztA6Ks0ve1aMcvmkcgXtQOW74oEwZ638L49IDsGPX//////////wE=" });
        
        bimg_alipay.putToImg(await select('img[src="/Third-Person/donate/alipay.jpg"]'));
        bimg_afdian.putToImg(await select('img[src="/Third-Person/donate/afdian.jpg"]'));
        bimg_bmc.putToImg(await select('img[src="/Third-Person/donate/bmc.jpg"]'));

        async function sleep(t = 0) {
            return new Promise(resolve => setTimeout(resolve, t));
        }
    }

    async function select(selector, interval = 200) {
        return new Promise(resolve => {
            const detectLoop = setInterval(() => {
                const result = document.querySelector(selector);
                if (result !== null) {
                    clearInterval(detectLoop);
                    resolve(result);
                }
            }, interval);
        });
    }
})();

</script>
