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
	const sidebar = [parseDir(dir)];
	// console.debug(JSON.stringify(sidebar, null, 2));
	return sidebar;

	/**
	 * @param dir 目录路径
	 * @param [name=null] 显示名称
	 */
	function parseDir(dir: string, name: string | null = null) {
		const dirName = name || getTitle(dir);
		return {
			text: dirName,
			collapsed: true,
			link: path.relative(docsRoot, dir).replace(/(^\/*)|(\/*$)/g, '/'),
			items: fs.readdirSync(dir).map(oName => {
				const oPath = path.join(dir, oName);
				if (!isOrHasPageFile(oPath)) return;
				if (fs.statSync(oPath).isFile()) {
					if (oName.startsWith('index')) return;
					return {
						text: getTitle(oPath),
						link: '/' + path.relative(docsRoot, oPath),
					};
				} else {
					return parseDir(oPath);
				}
			}).filter(i => i),
		};
	}
}

export function sidebarTree(dir: string, name: string, items: string[], docsRoot: string = 'docs') {
	dir = path.join(docsRoot, dir.replace(/^\/+/g, ''));
	const sidebar = [{
		text: name,
		items: items.map(oName => {
			const oPath = path.join(dir, oName);
			if (!fs.existsSync(oPath)) throw new Error(`File not found: ${oPath}`);
			if (fs.statSync(oPath).isFile()) {
				if (oName.startsWith('index')) return;
				return {
					text: getTitle(oPath),
					link: '/' + path.relative(docsRoot, oPath),
				};
			} else {
				return sidebarTree(oPath, oName, fs.readdirSync(oPath), docsRoot);
			}
		})
	}];

}

function getTitle(filePath: string): string {
	if (!fs.existsSync(filePath))
		return path.basename(filePath);
	const stat = fs.statSync(filePath);
	if (stat.isFile()) {
		const src: string = fs.readFileSync(filePath, 'utf-8');
		const matches = /(\n|^)\s*#*#(.*)/.exec(src);
		return matches === null ? path.basename(filePath) : matches[2];
	} else if (stat.isDirectory()) {
		return getTitle(path.join(filePath, 'index.md'));
	} else {
		return path.basename(filePath);
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
