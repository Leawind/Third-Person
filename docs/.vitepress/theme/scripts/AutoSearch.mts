/**
 * https://leawind.github.io/zh-CN/?search=%s
 * 
 * 如果没有 search 参数，则无操作。
 * 
 * 自动打开搜索框，将url参数中 search 的值填入框中。
 */
async function AutoSearch() {
	const question = getQuestionFromUrl();
	if (question === null) return;

	console.debug('Question from search param: ', question);

	console.debug("Waiting for document loading ...");
	while (document.readyState !== 'complete') await sleep();

	console.debug('Auto open search box');
	await openSearch();

	const ele_search_input: HTMLInputElement = await select("#localsearch-input") as HTMLInputElement;

	console.debug('Focus on input element: ', ele_search_input);
	ele_search_input.focus();

	console.debug("Update question from url to sessionStorage and input.value");
	sessionStorage['vitepress:local-search-filter'] = question;
	ele_search_input.value = question;

	ele_search_input.addEventListener('input', e => setQuestionToUrl(ele_search_input.value));

	console.debug("Auto dispatching input event to update search result");
	ele_search_input.dispatchEvent(new InputEvent('input'));
	while (true) {
		await sleep(0);
		if (!isSearchOpen())
			setQuestionToUrl(null);
	}
}
export default AutoSearch;

async function sleep(t = 0) {
	return new Promise(resolve => setTimeout(resolve, t));
}

async function select(selector, interval = 200): Promise<HTMLElement> {
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

/**
 * 打开搜索框
 */
async function openSearch() {
	if (isSearchOpen()) return;
	const ele_button = await select("#local-search > button");
	ele_button.click();
}
/**
 * 是否已经打开搜索框
 */
function isSearchOpen() {
	return document.querySelector('form.search-bar') !== null;
}

/**
 * 设置url中的search参数
 * 
 * 若传入null，则去掉search参数
 */
function setQuestionToUrl(question) {
	const url = new URL(location.href);
	question === null
		? url.searchParams.delete('search')
		: url.searchParams.set('search', question);
	history.pushState(null, '', url);
}
function getQuestionFromUrl() {
	const url = new URL(location.href);
	return url.searchParams.get('search');
}
