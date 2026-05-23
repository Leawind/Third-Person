const VERSION_LINE_REGEX =
  /^## v([\d.]+(-(alpha|beta)\.\d+)?)-mc([.\d]+)(-([.\d+]+))?$/
const VERSION_REGEX = /^([\d.]+)(-(alpha|beta)\.(\d+))?$/

/**
 * (a > b)  ->  1
 * (a = b)  ->  0
 * (a < b)  -> -1
 */
export function compareVersion(a: string, b: string): number {
  const matchA = VERSION_REGEX.exec(a)
  const matchB = VERSION_REGEX.exec(b)
  if (!matchA) {
    throw new Error(`Invalid version string: ${a}`)
  }
  if (!matchB) {
    throw new Error(`Invalid version string: ${b}`)
  }
  const [, release_a, , channel_a, channel_id_a] = matchA
  const [, release_b, , channel_b, channel_id_b] = matchB
  {
    const splited_a = release_a.split('.').map(Number)
    const splited_b = release_b.split('.').map(Number)
    const max_len = Math.max(splited_a.length, splited_b.length)
    for (let i = 0; i < max_len; i++) {
      const va = splited_a[i] || 0
      const vb = splited_b[i] || 0
      if (va > vb) { return 1 }
      if (va < vb) { return -1 }
    }
  }
  {
    if (!channel_a || channel_a > channel_b) { return 1 }
    if (channel_a < channel_b || channel_b) { return -1 }
  }
  {
    if (channel_id_a > channel_id_b) { return 1 }
    if (channel_id_a < channel_id_b) { return -1 }
  }
  return 0
}

export class Table {
  #cols: number
  #headers: string[]
  #rows_data: string[][] = []

  constructor() {
    this.#cols = 0
    this.#headers = []
  }

  #checkIndex(rowId: number, colId: number) {
    if (!(0 <= rowId && rowId < this.rows)) {
      throw new Error(`row index out of bounds: ${rowId}`)
    }
    if (!(0 <= colId && colId < this.cols)) {
      throw new Error(`col index out of bounds: ${colId}`)
    }
  }

  get cols(): number {
    return this.#cols
  }
  get rows(): number {
    return this.#rows_data.length
  }

  get(
    rowId: number,
    col: string | number,
    defaults: string | null = null,
  ): string | null {
    const colId = typeof col === 'string' ? this.getColId(col) : col
    this.#checkIndex(rowId, colId)
    const rawValue = this.#rows_data[rowId][colId]
    return rawValue === null ? defaults : rawValue
  }

  set(rowId: number, col: string | number, value: string) {
    const colId = typeof col === 'string' ? this.getColId(col) : col
    this.#checkIndex(rowId, colId)
    this.#rows_data[rowId][colId] = value
  }

  getColId(col: string): number {
    const id = this.#headers.indexOf(col)
    if (id < 0) { throw new Error(`Column not found: ${col}`) }
    return id
  }

  hasColumn(name: string): boolean {
    return this.#headers.indexOf(name) >= 0
  }

  insertColumn(colId: number, name: string | null = null): number {
    this.#cols++
    for (const row of this.#rows_data) {
      row.splice(colId, 0, null as unknown as string)
    }
    this.#headers.splice(colId, 0, name || '')
    return colId
  }

  insertRow(rowId: number, data: (string | null)[] = []): number {
    for (let i = data.length; i < this.cols; i++) {
      data[i] = null
    }
    this.#rows_data.splice(rowId, 0, data as string[])
    return rowId
  }

  rowIdOf(predicate: (rowId: number) => boolean): number {
    for (let i = 0; i < this.rows; i++) {
      if (predicate(i)) { return i }
    }
    return -1
  }

  sortRows(
    col: string,
    comparator: (a: string, b: string) => number,
  ) {
    const colId = this.getColId(col)
    this.#rows_data.sort((rowA, rowB) => {
      return comparator(rowA[colId], rowB[colId])
    })
  }

  sortColsByHeader(comparator: (a: string, b: string) => number) {
    const old_headers = [...this.#headers]
    this.#headers.sort(comparator)

    for (let rowId = 0; rowId < this.rows; rowId++) {
      const oldRow = this.#rows_data[rowId]
      const newRow: string[] = []
      for (let newColId = 0; newColId < this.cols; newColId++) {
        const newColName = this.#headers[newColId]
        const oldColId = old_headers.indexOf(newColName)
        newRow[newColId] = oldRow[oldColId]
      }
      this.#rows_data[rowId] = newRow
    }
  }

  toMarkdown(defaults = ''): string {
    // calculate max width of each column
    const widths: number[] = []
    for (let colId = 0; colId < this.cols; colId++) {
      let max_width = this.#headers[colId].length
      max_width = Math.max(max_width, 1)
      for (let rowId = 0; rowId < this.rows; rowId++) {
        const value = (this.get(rowId, colId, '') || '').toString()
        max_width = Math.max(max_width, value.length)
      }
      widths.push(max_width)
    }

    let md = ''
    // Head line
    {
      md += '|'
      for (let colId = 0; colId < this.cols; colId++) {
        const header = this.#headers[colId] || ''
        md += ` ${header.padEnd(widths[colId])} |`
      }
      md += '\n'
    }
    // spliter
    {
      md += '|'
      for (const width of widths) {
        md += ` ${'-'.repeat(width)} |`
      }
      md += '\n'
    }
    // rows
    {
      for (let rowId = 0; rowId < this.rows; rowId++) {
        md += '|'
        for (let colId = 0; colId < this.cols; colId++) {
          const value = (this.get(rowId, colId, defaults) || '').toString()
          md += ` ${value.padEnd(widths[colId])} |`
        }
        md += '\n'
      }
    }
    return md
  }
}

export class ModRelease {
  #mod_version: string
  #channel: string | undefined
  #mc_version_min: string
  #mc_version_max: string | undefined

  constructor(line: string) {
    const match = VERSION_LINE_REGEX.exec(line)
    if (!match) { throw new Error(`Invalid version line: ${line}`) }
    const [, mod_version, , channel, mc_version_min, , mc_version_max] = match
    this.#mod_version = mod_version
    this.#channel = channel
    this.#mc_version_min = mc_version_min
    this.#mc_version_max = mc_version_max
  }

  get modVersion(): string {
    return this.#mod_version
  }
  get channel(): string | undefined {
    return this.#channel
  }
  get mcVersionMin(): string {
    return this.#mc_version_min
  }
  get mcVersionMax(): string | undefined {
    return this.#mc_version_max
  }
  get isMcVersionRanged(): boolean {
    return !!this.mcVersionMax
  }
  get mcVersions(): string[] {
    return [
      this.mcVersionMin,
      ...(this.isMcVersionRanged ? [this.mcVersionMax!] : []),
    ]
  }
  toString(): string {
    return `${this.modVersion}-mc${this.mcVersionMin}${
      this.isMcVersionRanged ? `-${this.mcVersionMax}` : ''
    }`
  }
}

export function loadModVersionsFromChangelog(
  filePath: string,
): ModRelease[] {
  const md = Deno.readTextFileSync(filePath)
  const versions: ModRelease[] = []

  md.split('\n')
    .map((line) => VERSION_LINE_REGEX.exec(line))
    .filter((match): match is RegExpExecArray => !!match)
    .forEach((match) => {
      const ver = new ModRelease(match[0])
      versions.push(ver)
    })

  return versions
}
