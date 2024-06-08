import fs from 'fs';
import path from 'path/posix';

import { DefaultTheme } from 'vitepress/theme';

type FileDescriptor = {
	// Directory title
	title: string;
	src?: string;
	// There is only a title in the title file
	titleOnly?: boolean;
};

/**
 * Auto build side bar
 * 
 * @param dir  Directory path
 * @param docsRoot Doc root
 */
export function buildSidebar(dir: string, docsRoot: string = 'docs'): DefaultTheme.SidebarItem[] {
	let dirPath = path.join(docsRoot, dir.replace(/^\/+/g, ''));
	const sidebar = [buildDir(dirPath, false)];
	// console.debug(JSON.stringify(sidebar, null, 2));
	return sidebar;

	/**
	 * Build sidebar of directory
	 * 
	 * @param dirPath Directory path
	 */
	function buildDir(dirPath: string, collapsed = true): DefaultTheme.SidebarItem {
		const dir = parseFile(dirPath);
		const result: DefaultTheme.SidebarItem = {
			text: dir.title,
			collapsed,
			items: (() => {
				const items: DefaultTheme.SidebarItem[] = [];
				const subNames = fs.readdirSync(dirPath);
				subNames.sort();
				for (const subName of subNames) {
					const subPath = path.join(dirPath, subName);
					if (isOrHasPageFile(subPath)) {
						const subStat = fs.statSync(subPath);
						if (subStat.isFile()) {
							if (subName.startsWith('index')) continue;
							items.push({
								text: parseFile(subPath).title,
								link: '/' + path.relative(docsRoot, subPath),
							});
						} else if (subStat.isDirectory()) {
							const subdirSidebar = buildDir(subPath, true);
							items.push(subdirSidebar);
						}
					}
				}
				return items;
			})(),

		};
		if (!dir.titleOnly)
			result.link = path.relative(docsRoot, dirPath).replace(/(^\/*)|(\/*$)/g, '/');
		return result;
	}

	/**
	 * Read file or directory metadata
	 */
	function parseFile(filePath: string): FileDescriptor {
		const defaultTitle = path.basename(filePath);
		if (fs.existsSync(filePath)) {
			const stat = fs.statSync(filePath);
			if (stat.isFile()) {
				const srcFile: string = fs.readFileSync(filePath, 'utf-8');
				const matches = /(\n|^)\s*#+\s*(.*)/.exec(srcFile);
				return {
					title: matches === null ? defaultTitle : matches[2],
					src: srcFile,
					titleOnly: srcFile.replace(/(\n|^)\s*#+\s*.*(\n|$)/, '').trim() === '',
				};
			} else if (stat.isDirectory()) {
				const indexFilePath = path.join(filePath, 'index.md');
				if (fs.existsSync(indexFilePath)) {
					return parseFile(indexFilePath);
				}
			}
		}
		return {
			title: defaultTitle,
			titleOnly: true,
		};
	}

	/**
	 * Check if the specified path is a page file or a directory contains any page file.
	 */
	function isOrHasPageFile(apath: string) {
		const stat = fs.statSync(apath);
		if (stat.isFile()) {
			return /.*\.(md)|(html)|(htm)$/i.test(apath);
		} else if (stat.isDirectory()) {
			for (const subName of fs.readdirSync(apath)) {
				const subPath = path.join(apath, subName);
				if (isOrHasPageFile(subPath)) return true;
			}
			return false;
		}
	}
}
