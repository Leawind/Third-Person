#!/usr/bin/env node

import util from './util.mjs';
import fs from 'fs';

const TABLE_MD_PATH = lang => `./docs/${lang}/Details/McVersions.md`;
const CHANGELOG_PATH = './docs/zh-CN/changelog.md';
const TABLE_REGEX = /(?<=<div id="mv">)(.|\n)*(?=<\/div>)/;
const YES_CHAR = '✅';
const BLANK_CHAR = ' ';

generate('zh-CN');
generate('en-US');

function generate(lang) {
	const versions = util.loadModVersionsFromChangelog(CHANGELOG_PATH);
	const table = new util.Table();

	table.insertColumn(0, 'Mod');

	// Write table
	versions.forEach(v => {
		let rowId = table.rowIdOf(rowId => table.get(rowId, 'Mod') === v.modVersion);
		// Row
		if (rowId < 0) {
			rowId = table.insertRow(0, []);
		}

		for (const mc_version of v.mcVersions) {
			// Column
			if (!table.hasColumn(mc_version)) {
				table.insertColumn(-1, mc_version);
			}

			table.set(rowId, mc_version, YES_CHAR);
		}
		table.set(rowId, 'Mod', `${v.modVersion}`);
	});

	// Sort
	{
		table.sortColsByHeader((a, b) => {
			if (a === 'Mod') return -1;
			if (b === 'Mod') return -1;
			return util.compareVersion(a, b);
		});
		table.sortRows('Mod', (a, b) => -util.compareVersion(a, b));
	}

	{
		for (let rowId = 0; rowId < table.rows; rowId++) {
			let v = table.get(rowId, 'Mod');
			v = '`' + v + '`';
			table.set(rowId, 'Mod', v);
		}
	}

	const TableMarkdownPath = TABLE_MD_PATH(lang);
	let md = fs.readFileSync(TableMarkdownPath, 'utf-8');
	md = md.replace(TABLE_REGEX, '\n\n' + table.toMarkdown(BLANK_CHAR) + '\n\n');
	fs.writeFileSync(TableMarkdownPath, md, 'utf-8');
	// console.log(`${lang}:\n${md}`);
}
