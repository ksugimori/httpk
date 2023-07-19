window.addEventListener('load', event => {

    const form = document.querySelector('#sum_form');
    const sum = document.querySelector('#sum')

    form.addEventListener('submit', (e) => {
        e.preventDefault();

        // FormData を POST すると multipart/form-data になって実装が面倒なので、URLSearchParams を使う
        // この場合 Content-Type は application/x-www-form-urlencoded で、通常の form 送信と同じ
        const formData = new FormData(form);
        const params = new URLSearchParams();
        for (const e of formData.entries()) {
            params.append(e[0], e[1]);
        }

        fetch('/api/sum', {
            method: 'POST',
            body: params,
        })
        .then(response =>  response.json())
        .then(data => {
            sum.textContent = data.sum;
        });
    });
});