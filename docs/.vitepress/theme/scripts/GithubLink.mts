async function GithubLink() {
	while (true) {
		await new Promise(resolve => setTimeout(resolve, 1000));
		const lis = document.querySelectorAll('ul>li');
		for (const li of lis) {
			li.innerHTML = li.innerHTML.replace(/#(\d+)$/, `<a href="https://github.com/Leawind/Third-Person/issues/$1">#$1</a>`);
		}
	}
}

export default GithubLink;
