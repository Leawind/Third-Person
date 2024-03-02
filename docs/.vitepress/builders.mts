import fs from 'fs';
import path from 'path/posix';
/**
 * 自动构建侧栏
 * 
 * @param dir  目录路径
 * @param name 侧栏名称
 * @param [docsRoot='docs'] 文档根目录
 */
export function buildSidebar(dir: string, docsRoot: string = 'docs'): any {
	dir = path.join(docsRoot, dir.replace(/^\/+/g, ''));
	const sidebar = [buildDir(dir)];
	// console.debug(JSON.stringify(sidebar, null, 2));
	return sidebar;

	/**
	 * @param dirPath 目录路径
	 * @param [name=null] 显示名称
	 */
	function buildDir(dirPath: string) {
		const dir = parseDir(dirPath);
		const result = {
			text: dir.title,
			collapsed: true,
			items: fs.readdirSync(dirPath).map(oName => {
				const oPath = path.join(dirPath, oName);
				if (!isOrHasPageFile(oPath)) return;
				if (fs.statSync(oPath).isFile()) {
					if (oName.startsWith('index')) return;
					return {
						text: parseDir(oPath).title,
						link: '/' + path.relative(docsRoot, oPath),
					};
				} else {
					return buildDir(oPath);
				}
			}).filter(i => i),
		};
		if (!dir.titleOnly)
			result.link = path.relative(docsRoot, dirPath).replace(/(^\/*)|(\/*$)/g, '/');
		return result;
	}
}

function parseDir(filePath: string): { title: string; src?: string; titleOnly?: boolean; } {
	if (!fs.existsSync(filePath))
		return { title: path.basename(filePath) };
	const stat = fs.statSync(filePath);
	if (stat.isFile()) {
		const src: string = fs.readFileSync(filePath, 'utf-8');
		const matches = /(\n|^)\s*#*#(.*)/.exec(src);
		return {
			title: matches === null ? path.basename(filePath) : matches[2],
			src,
			titleOnly: src.replace(/(\n|^)\s*#{1,}.*(\n|$)/, '').trim() === '',
		};
	} else if (stat.isDirectory()) {
		return parseDir(path.join(filePath, 'index.md'));
	} else {
		return { title: path.basename(filePath) };
	}
}

function isPageFile(filePath) {
	return /\.(md)|(html)|(htm)/i.test(path.parse(filePath).ext);
}

function hasPageFile(dirPath: string) {
	for (const subname of fs.readdirSync(dirPath))
		if (isPageFile(subname)) return true;
	return false;
}

function isOrHasPageFile(apath: string) {
	if (fs.statSync(apath).isFile()) return isPageFile(apath);
	else return hasPageFile(apath);
}
