<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">博客编辑器</h2>
      <div class="space-x-3">
        <el-button @click="goToList">返回列表</el-button>
        <el-button type="primary" @click="publishBlog" :icon="Document" :loading="publishing">
          发布文章
        </el-button>
      </div>
    </div>
    
    <el-card class="editor-header">
      <el-input
        v-model="title"
        placeholder="请输入文章标题"
        size="large"
        class="title-input"
      />
    </el-card>
    
    <el-card class="editor-container">
      <div class="toolbar">
        <el-tooltip content="标题 H1" placement="bottom">
          <button class="toolbar-btn" @click="insertHeading(1)">
            <span class="text-lg font-bold">H1</span>
          </button>
        </el-tooltip>
        <el-tooltip content="标题 H2" placement="bottom">
          <button class="toolbar-btn" @click="insertHeading(2)">
            <span class="text-base font-bold">H2</span>
          </button>
        </el-tooltip>
        <el-tooltip content="标题 H3" placement="bottom">
          <button class="toolbar-btn" @click="insertHeading(3)">
            <span class="text-sm font-bold">H3</span>
          </button>
        </el-tooltip>
        
        <span class="toolbar-divider"></span>
        
        <el-tooltip content="粗体" placement="bottom">
          <button class="toolbar-btn" @click="insertBold">
            <span class="font-bold">B</span>
          </button>
        </el-tooltip>
        <el-tooltip content="斜体" placement="bottom">
          <button class="toolbar-btn" @click="insertItalic">
            <span class="italic">I</span>
          </button>
        </el-tooltip>
        <el-tooltip content="删除线" placement="bottom">
          <button class="toolbar-btn" @click="insertStrikethrough">
            <span class="line-through">S</span>
          </button>
        </el-tooltip>
        
        <span class="toolbar-divider"></span>
        
        <el-tooltip content="有序列表" placement="bottom">
          <button class="toolbar-btn" @click="insertOrderedList">
            <el-icon><List /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="无序列表" placement="bottom">
          <button class="toolbar-btn" @click="insertUnorderedList">
            <el-icon><List /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="任务列表" placement="bottom">
          <button class="toolbar-btn" @click="insertTaskList">
            <el-icon><Check /></el-icon>
          </button>
        </el-tooltip>
        
        <span class="toolbar-divider"></span>
        
        <el-tooltip content="引用" placement="bottom">
          <button class="toolbar-btn" @click="insertQuote">
            <el-icon><Promotion /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="代码" placement="bottom">
          <button class="toolbar-btn" @click="insertCode">
            <el-icon><Cpu /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="代码块" placement="bottom">
          <button class="toolbar-btn" @click="insertCodeBlock">
            <el-icon><Document /></el-icon>
          </button>
        </el-tooltip>
        
        <span class="toolbar-divider"></span>
        
        <el-tooltip content="链接" placement="bottom">
          <button class="toolbar-btn" @click="insertLink">
            <el-icon><Link /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="图片" placement="bottom">
          <button class="toolbar-btn" @click="triggerImageUpload">
            <el-icon><Picture /></el-icon>
          </button>
          <input
            ref="imageInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleImageSelect"
          />
        </el-tooltip>
        <el-tooltip content="表格" placement="bottom">
          <button class="toolbar-btn" @click="insertTable">
            <el-icon><Grid /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="分割线" placement="bottom">
          <button class="toolbar-btn" @click="insertDivider">
            <el-icon><Minus /></el-icon>
          </button>
        </el-tooltip>
        
        <span class="toolbar-divider"></span>
        
        <el-tooltip content="撤销" placement="bottom">
          <button class="toolbar-btn" @click="undo">
            <el-icon><RefreshLeft /></el-icon>
          </button>
        </el-tooltip>
        <el-tooltip content="重做" placement="bottom">
          <button class="toolbar-btn" @click="redo">
            <el-icon><RefreshRight /></el-icon>
          </button>
        </el-tooltip>
      </div>
      
      <div class="editor-layout">
        <div class="editor-pane">
          <div class="pane-header">
            <span class="pane-title">Markdown 编辑</span>
            <span class="text-xs text-gray-500 ml-2">提示：可直接粘贴图片</span>
          </div>
          <textarea
            ref="textareaRef"
            v-model="content"
            placeholder="开始编写你的 Markdown 内容...&#10;&#10;快捷键：&#10;Ctrl+B 粗体&#10;Ctrl+I 斜体&#10;Ctrl+K 链接&#10;可直接粘贴图片"
            class="editor-textarea"
            @paste="handlePaste"
            @keydown="handleKeydown"
          ></textarea>
        </div>
        
        <div class="editor-divider"></div>
        
        <div class="preview-pane">
          <div class="pane-header">
            <span class="pane-title">实时预览</span>
          </div>
          <div class="preview-content markdown-body" v-html="renderedContent"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { Document } from '@element-plus/icons-vue'
import {
  List,
  Check,
  Promotion,
  Cpu,
  Link,
  Picture,
  Grid,
  Minus,
  RefreshLeft,
  RefreshRight
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import { blogService } from '../services/api'

marked.setOptions({
  breaks: true,
  gfm: true
})

const router = useRouter()
const title = ref('')
const content = ref('')
const publishing = ref(false)
const textareaRef = ref(null)
const imageInput = ref(null)

const history = ref([])
const historyIndex = ref(-1)
const MAX_HISTORY = 50

const renderedContent = computed(() => {
  if (!content.value) return '<p class="text-gray-400 italic">内容区域为空，开始输入 Markdown 内容...</p>'
  return marked(content.value)
})

const saveToHistory = () => {
  if (historyIndex.value < history.value.length - 1) {
    history.value = history.value.slice(0, historyIndex.value + 1)
  }
  history.value.push(content.value)
  if (history.value.length > MAX_HISTORY) {
    history.value.shift()
  } else {
    historyIndex.value++
  }
}

const undo = () => {
  if (historyIndex.value > 0) {
    historyIndex.value--
    content.value = history.value[historyIndex.value]
  }
}

const redo = () => {
  if (historyIndex.value < history.value.length - 1) {
    historyIndex.value++
    content.value = history.value[historyIndex.value]
  }
}

const insertText = (before, after = '', defaultText = '') => {
  const textarea = textareaRef.value
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = content.value.substring(start, end)
  const insertText = selectedText || defaultText
  
  saveToHistory()
  
  content.value = 
    content.value.substring(0, start) +
    before + insertText + after +
    content.value.substring(end)
  
  nextTick(() => {
    textarea.focus()
    const newPos = start + before.length + insertText.length
    textarea.setSelectionRange(newPos, newPos)
  })
}

const insertWrap = (before, after = '') => {
  const textarea = textareaRef.value
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = content.value.substring(start, end)
  
  saveToHistory()
  
  content.value = 
    content.value.substring(0, start) +
    before + selectedText + after +
    content.value.substring(end)
  
  nextTick(() => {
    textarea.focus()
    if (selectedText) {
      textarea.setSelectionRange(start + before.length, end + before.length)
    } else {
      const newPos = start + before.length
      textarea.setSelectionRange(newPos, newPos)
    }
  })
}

const insertLine = (prefix) => {
  const textarea = textareaRef.value
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  
  let lineStart = start
  while (lineStart > 0 && content.value[lineStart - 1] !== '\n') {
    lineStart--
  }
  
  saveToHistory()
  
  const newline = (lineStart === 0 ? '' : '\n')
  content.value = 
    content.value.substring(0, lineStart) +
    newline + prefix + ' ' +
    content.value.substring(lineStart)
  
  nextTick(() => {
    textarea.focus()
    const newPos = lineStart + newline.length + prefix.length + 1
    textarea.setSelectionRange(newPos, newPos)
  })
}

const insertHeading = (level) => {
  const prefix = '#'.repeat(level)
  insertLine(prefix)
}

const insertBold = () => {
  insertWrap('**', '**')
}

const insertItalic = () => {
  insertWrap('*', '*')
}

const insertStrikethrough = () => {
  insertWrap('~~', '~~')
}

const insertOrderedList = () => {
  insertLine('1.')
}

const insertUnorderedList = () => {
  insertLine('-')
}

const insertTaskList = () => {
  insertLine('- [ ]')
}

const insertQuote = () => {
  insertLine('>')
}

const insertCode = () => {
  insertWrap('`', '`')
}

const insertCodeBlock = () => {
  const textarea = textareaRef.value
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = content.value.substring(start, end)
  
  saveToHistory()
  
  const codeBlock = `\n\`\`\`javascript\n${selectedText || '// 在此输入代码'}\n\`\`\`\n`
  
  content.value = 
    content.value.substring(0, start) +
    codeBlock +
    content.value.substring(end)
  
  nextTick(() => {
    textarea.focus()
    const newPos = start + 4 + 12
    textarea.setSelectionRange(newPos, newPos)
  })
}

const insertLink = async () => {
  const { value: formValues } = await ElMessage({
    message: '请输入链接信息',
    type: 'info',
    showClose: true
  })
  
  const linkText = '链接文本'
  const linkUrl = 'https://'
  
  insertWrap(`[${linkText}](`, `${linkUrl})`)
}

const insertImage = (altText, imageUrl) => {
  insertText(`![${altText}](`, `)`, imageUrl)
}

const insertTable = () => {
  const table = `
| 表头1 | 表头2 | 表头3 |
| --- | --- | --- |
| 内容1 | 内容2 | 内容3 |
| 内容4 | 内容5 | 内容6 |
`
  insertText('', '', table)
}

const insertDivider = () => {
  insertText('\n---\n', '', '')
}

const triggerImageUpload = () => {
  imageInput.value?.click()
}

const handleImageSelect = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    processImageFile(file)
  }
  if (imageInput.value) {
    imageInput.value.value = ''
  }
}

const processImageFile = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  
  const reader = new FileReader()
  reader.onload = (e) => {
    const base64 = e.target?.result
    if (base64) {
      const fileName = file.name.replace(/\.[^/.]+$/, '')
      saveToHistory()
      
      const textarea = textareaRef.value
      if (textarea) {
        const start = textarea.selectionStart
        const end = textarea.selectionEnd
        const imageMarkdown = `\n![${fileName}](${base64})\n`
        
        content.value = 
          content.value.substring(0, start) +
          imageMarkdown +
          content.value.substring(end)
        
        nextTick(() => {
          const newPos = start + imageMarkdown.length
          textarea.setSelectionRange(newPos, newPos)
        })
      }
      
      ElMessage.success('图片已插入')
    }
  }
  reader.readAsDataURL(file)
}

const handlePaste = (event) => {
  const items = event.clipboardData?.items
  if (!items) return
  
  for (const item of items) {
    if (item.type.startsWith('image/')) {
      event.preventDefault()
      const file = item.getAsFile()
      if (file) {
        processImageFile(file)
      }
      return
    }
  }
}

const handleKeydown = (event) => {
  if (event.ctrlKey || event.metaKey) {
    switch (event.key.toLowerCase()) {
      case 'b':
        event.preventDefault()
        insertBold()
        break
      case 'i':
        event.preventDefault()
        insertItalic()
        break
      case 'k':
        event.preventDefault()
        insertLink()
        break
      case 'z':
        if (event.shiftKey) {
          event.preventDefault()
          redo()
        }
        break
    }
  }
}

const publishBlog = async () => {
  if (!title.value.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!content.value.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  publishing.value = true
  try {
    await blogService.add({
      title: title.value.trim(),
      content: content.value
    })
    ElMessage.success('文章发布成功')
    router.push('/blogs')
  } catch (error) {
    console.error('Failed to publish blog:', error)
    ElMessage.error('发布失败')
  } finally {
    publishing.value = false
  }
}

const goToList = () => {
  router.push('/blogs')
}
</script>

<style scoped>
.editor-header {
  margin-bottom: 16px;
}

.title-input {
  font-size: 18px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 8px 12px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  flex-wrap: wrap;
}

.toolbar-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  border-radius: 4px;
  cursor: pointer;
  color: #4b5563;
  transition: all 0.2s;
}

.toolbar-btn:hover {
  background-color: #e5e7eb;
  color: #1f2937;
}

.toolbar-divider {
  width: 1px;
  height: 24px;
  background-color: #d1d5db;
  margin: 0 4px;
}

.editor-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 340px);
  min-height: 400px;
}

.editor-layout {
  display: flex;
  flex: 1;
  gap: 0;
  min-height: 0;
}

.editor-pane,
.preview-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.editor-divider {
  width: 1px;
  background-color: #e5e7eb;
  margin: 0 8px;
}

.pane-header {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.pane-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.editor-textarea {
  flex: 1;
  width: 100%;
  border: none;
  outline: none;
  resize: none;
  padding: 16px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
  line-height: 1.8;
  background-color: #fafafa;
  overflow-y: auto;
}

.editor-textarea:focus {
  background-color: #ffffff;
}

.preview-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.preview-content :deep(h1) {
  font-size: 2em;
  font-weight: bold;
  margin: 0.67em 0;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 0.3em;
}

.preview-content :deep(h2) {
  font-size: 1.5em;
  font-weight: bold;
  margin: 0.83em 0;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 0.3em;
}

.preview-content :deep(h3) {
  font-size: 1.25em;
  font-weight: bold;
  margin: 1em 0;
}

.preview-content :deep(h4) {
  font-size: 1em;
  font-weight: bold;
  margin: 1.33em 0;
}

.preview-content :deep(p) {
  margin: 1em 0;
  line-height: 1.8;
}

.preview-content :deep(ul),
.preview-content :deep(ol) {
  padding-left: 2em;
  margin: 1em 0;
}

.preview-content :deep(li) {
  margin: 0.25em 0;
  line-height: 1.6;
}

.preview-content :deep(blockquote) {
  margin: 1em 0;
  padding: 0.5em 1em;
  border-left: 4px solid #3b82f6;
  background-color: #f0f9ff;
  color: #1e40af;
}

.preview-content :deep(code) {
  background-color: #f3f4f6;
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 0.9em;
  color: #dc2626;
}

.preview-content :deep(pre) {
  background-color: #1f2937;
  padding: 1em;
  border-radius: 6px;
  overflow-x: auto;
  margin: 1em 0;
}

.preview-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
  color: #f9fafb;
}

.preview-content :deep(a) {
  color: #3b82f6;
  text-decoration: underline;
}

.preview-content :deep(a:hover) {
  color: #1d4ed8;
}

.preview-content :deep(hr) {
  border: none;
  border-top: 2px solid #e5e7eb;
  margin: 2em 0;
}

.preview-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1em 0;
}

.preview-content :deep(th),
.preview-content :deep(td) {
  border: 1px solid #e5e7eb;
  padding: 0.5em 1em;
  text-align: left;
}

.preview-content :deep(th) {
  background-color: #f9fafb;
  font-weight: bold;
}

.preview-content :deep(img) {
  max-width: 100%;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.preview-content :deep(strong) {
  font-weight: bold;
}

.preview-content :deep(em) {
  font-style: italic;
}

.preview-content :deep(del) {
  text-decoration: line-through;
  color: #6b7280;
}

.preview-content :deep(input[type="checkbox"]) {
  margin-right: 0.5em;
}
</style>
