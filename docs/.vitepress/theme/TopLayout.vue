<script setup>


import { useData } from 'vitepress';
globalThis.useData = useData;

import AutoLang from './scripts/AutoLang.mts';
import AutoSearch from './scripts/AutoSearch.mts';

if (globalThis.window) {
	AutoLang();
	AutoSearch();
}

</script>

<template></template>
