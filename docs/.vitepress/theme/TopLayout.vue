<script setup>


import { useData } from 'vitepress';
globalThis.useData = useData;

import AutoLang from './scripts/AutoLang.mts';
import AutoSearch from './scripts/AutoSearch.mts';
import GithubLink from './scripts/GithubLink.mts';

if (globalThis.window) {
	AutoLang();
	AutoSearch();
	GithubLink();
}

</script>

<template></template>
