import fs from 'fs';

const util = {
	VERSION_LINE_REGEX: /^## v([\d\.]+(-(alpha|beta)\.\d+)?)-mc([\.\d]+)(-([\.\d+]+))?$/,
	VERSION_REGEX: /^([\d\.]+)(-(alpha|beta)\.(\d+))?$/,

	/**
	 * (a > b)  ->  1
	 * (a = b)  ->  0
	 * (a < b)  -> -1
	 * @returns {number}
	 */
	compareVersion(a, b) {
		const matchA = util.VERSION_REGEX.exec(a);
		const matchB = util.VERSION_REGEX.exec(b);
		if (!matchA) {
			throw new Error(`Invalid version string: ${a}`);
		}
		if (!matchB) {
			throw new Error(`Invalid version string: ${b}`);
		}
		const [a0, release_a, a2, channel_a, channel_id_a] = matchA;
		const [b0, release_b, b2, channel_b, channel_id_b] = matchB;
		{
			const splited_a = release_a.split('.').map(Number);
			const splited_b = release_b.split('.').map(Number);
			const max_len = Math.max(splited_a.length, splited_b.length);
			for (let i = 0; i < max_len; i++) {
				const va = splited_a[i] || 0;
				const vb = splited_b[i] || 0;
				if (va > vb) return 1;
				if (va < vb) return -1;
			}
		}
		{
			if (!channel_a || channel_a > channel_b) return 1;
			if (channel_a < channel_b || channel_b) return -1;
		}
		{
			if (channel_id_a > channel_id_b) return 1;
			if (channel_id_a < channel_id_b) return -1;
		}
		return 0;
	},

	Table: class {
		/**
		 * @type {number}
		 */
		#cols;
		/**
		 * @type {string[]}
		 */
		#headers;
		/**
		 * @type {string[][]}
		 */
		#rows_data = [];
		constructor() {
			this.#cols = 0;
			this.#headers = [];
		}
		#checkIndex(rowId, colId) {
			if (!(0 <= rowId && rowId < this.rows)) {
				throw new Error(`row index out of bounds: ${rowId}`);
			}
			if (!(0 <= colId && colId < this.cols)) {
				throw new Error(`col index out of bounds: ${colId}`);
			}
		}
		get cols() {
			return this.#cols;
		}
		get rows() {
			return this.#rows_data.length;
		}
		get(rowId, col, defaults = null) {
			const colId = this.getColId(col);
			this.#checkIndex(rowId, colId);
			const rawValue = this.#rows_data[rowId][colId];
			return rawValue === null ? defaults : rawValue;
		}
		set(rowId, col, value) {
			const colId = this.getColId(col);
			this.#checkIndex(rowId, colId);
			this.#rows_data[rowId][colId] = value;
		}
		getColId(col) {
			let id = col;
			if (typeof col === 'string') {
				id = this.#headers.indexOf(col);
			}
			if (id < 0) throw new Error(`Column not found: ${col}`);
			return id;
		}
		hasColumn(name) {
			return this.#headers.indexOf(name) >= 0;
		}

		/**
		 * @param {number} colId
		 * @param {string[]} [data=[]]
		 * @returns {number}
		 */
		insertColumn(colId, name = null) {
			this.#cols++;
			for (const row of this.#rows_data) {
				row.splice(colId, 0, null);
			}
			this.#headers.splice(colId, '', name);
			return colId;
		}
		/**
		 * @param {number} rowId
		 * @param {string[]} [data=[]]
		 * @returns {number}
		 */
		insertRow(rowId, data = []) {
			for (let i = data.length; i < this.cols; i++) {
				data[i] = null;
			}
			this.#rows_data.splice(rowId, 0, data);
			return rowId;
		}

		/**
		 * @param {(number)=>boolean} predicate
		 */
		rowIdOf(predicate) {
			for (let i = 0; i < this.rows; i++) {
				if (predicate(i)) return i;
			}
			return -1;
		}

		sortRows(col, comparator) {
			const colId = this.getColId(col);
			this.#rows_data.sort((rowA, rowB) => {
				const a = rowA[colId];
				const b = rowB[colId];
				return comparator(a, b);
			});
		}

		sortColsByHeader(comparator) {
			const old_headers = [...this.#headers];
			this.#headers.sort(comparator);

			for (let rowId = 0; rowId < this.rows; rowId++) {
				const oldRow = this.#rows_data[rowId];
				const newRow = [...oldRow];
				for (let newColId = 0; newColId < this.cols; newColId++) {
					const newColName = this.#headers[newColId];
					const oldColId = old_headers.indexOf(newColName);
					newRow[newColId] = oldRow[oldColId];
				}
				this.#rows_data[rowId] = newRow;
			}
		}

		toMarkdown(defaults = '') {
			// calculate max width of each column
			const widths = [];
			for (let colId = 0; colId < this.cols; colId++) {
				let max_width = this.#headers[colId].length;
				max_width = Math.max(max_width, 1);
				for (let rowId = 0; rowId < this.rows; rowId++) {
					const value = this.get(rowId, colId, '').toString();
					max_width = Math.max(max_width, value.length);
				}
				widths.push(max_width);
			}

			let md = '';
			// Head line
			{
				md += '|';
				for (let colId = 0; colId < this.cols; colId++) {
					const header = this.#headers[colId] || '';
					md += ` ${header.padEnd(widths[colId])} |`;
				}
				md += '\n';
			}
			// spliter
			{
				md += '|';
				for (const width of widths) {
					md += ` ${'-'.repeat(width)} |`;
				}
				md += '\n';
			}
			// rows
			{
				for (let rowId = 0; rowId < this.rows; rowId++) {
					md += '|';
					for (let colId = 0; colId < this.cols; colId++) {
						const value = this.get(rowId, colId, defaults).toString();
						md += ` ${value.padEnd(widths[colId])} |`;
					}
					md += '\n';
				}
			}
			return md;
		}
	},

	ModRelease: class {
		#mod_version;
		#channel;
		#mc_version_min;
		#mc_version_max;

		/**
		 * @param {string} line
		 */
		constructor(line) {
			const match = util.VERSION_LINE_REGEX.exec(line);
			let [_, mod_version, _1, channel, mc_version_min, _2, mc_version_max] = match;
			this.#mod_version = mod_version;
			this.#channel = channel;
			this.#mc_version_min = mc_version_min;
			this.#mc_version_max = mc_version_max;
		}
		/**
		 * @returns {string}
		 */
		get modVersion() {
			return this.#mod_version;
		}
		/**
		 * @returns {Symbol}
		 */
		get channel() {
			return this.#channel;
		}
		/**
		 * @returns {string}
		 */
		get mcVersionMin() {
			return this.#mc_version_min;
		}
		/**
		 * @returns {string|null}
		 */
		get mcVersionMax() {
			return this.#mc_version_max;
		}
		/**
		 * @returns {boolean}
		 */
		get isMcVersionRanged() {
			return !!this.mcVersionMax;
		}
		/**
		 * @returns {string[]}
		 */
		get mcVersions() {
			return [this.mcVersionMin, ...(this.isMcVersionRanged ? [this.mcVersionMax] : [])];
		}
		toString() {
			return `${this.modVersion}-mc${this.mcVersionMin}${this.isMcVersionRanged ? `-${this.mcVersionMax}` : ''}`;
		}
	},
	/**
	 * @returns {util.ModRelease[]}
	 */
	loadModVersionsFromChangelog(filePath) {
		const md = fs.readFileSync(filePath, 'utf-8');

		const versions = [];

		md.split('\n')
			.map(line => util.VERSION_LINE_REGEX.exec(line))
			.filter(match => match)
			.forEach(match => {
				let ver = new util.ModRelease(match[0]);
				versions.push(ver);
			});

		return versions;
	},
};

export default util;
