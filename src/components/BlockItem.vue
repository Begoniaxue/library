<template>
  <div
    class="block-item-wrapper"
    :class="{
      'dragging': isDragging,
      'drag-over': isDragOver
    }"
    draggable="true"
    @dragstart="onDragStart"
    @dragend="onDragEnd"
    @dragover="onDragOver"
    @drop="onDrop"
  >
    <div class="block-item" :class="getBlockClass">
      <div class="block-handle" @mousedown="startDrag">
        <svg class="w-4 h-4 text-gray-400" viewBox="0 0 24 24" fill="currentColor">
          <circle cx="9" cy="6" r="1.5" />
          <circle cx="9" cy="12" r="1.5" />
          <circle cx="9" cy="18" r="1.5" />
          <circle cx="15" cy="6" r="1.5" />
          <circle cx="15" cy="12" r="1.5" />
          <circle cx="15" cy="18" r="1.5" />
        </svg>
      </div>

      <div class="block-content flex-1">
        <div v-if="block.type === 'paragraph'" class="block-paragraph">
          <div
            ref="inputRef"
            class="editable-content"
            contenteditable="true"
            :data-placeholder="placeholder"
            @input="onInput"
            @keydown="onKeyDown"
            @focus="onFocus"
            @blur="onBlur"
          ></div>
        </div>

        <div v-else-if="block.type === 'bullet-list'" class="block-list-container">
          <div class="list-header flex items-center mb-2">
            <span class="text-xs text-gray-500 mr-2">•</span>
            <span class="text-sm font-medium text-gray-600">无序列表</span>
            <el-button
              size="small"
              link
              class="ml-auto"
              @click="addListItem"
            >
              + 添加项目
            </el-button>
          </div>
          <div class="list-items space-y-1 pl-6">
            <div
              v-for="(child, childIndex) in block.children"
              :key="child.id"
              class="list-item"
            >
              <span class="list-bullet">•</span>
              <div
                :ref="el => setListInputRef(child.id, el)"
                class="editable-content flex-1"
                contenteditable="true"
                :data-placeholder="placeholder"
                @input="onListInput(child.id, $event)"
                @keydown="onListKeyDown(child.id, childIndex, $event)"
                @focus="onListFocus(child.id)"
                @blur="onBlur"
              ></div>
              <el-button
                size="small"
                link
                class="text-gray-400 hover:text-red-500 ml-2"
                @click="removeListItem(childIndex)"
              >
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <div v-else-if="block.type === 'numbered-list'" class="block-list-container">
          <div class="list-header flex items-center mb-2">
            <span class="text-xs text-gray-500 mr-2">1.</span>
            <span class="text-sm font-medium text-gray-600">有序列表</span>
            <el-button
              size="small"
              link
              class="ml-auto"
              @click="addListItem"
            >
              + 添加项目
            </el-button>
          </div>
          <div class="list-items space-y-1 pl-6">
            <div
              v-for="(child, childIndex) in block.children"
              :key="child.id"
              class="list-item"
            >
              <span class="list-number">{{ childIndex + 1 }}.</span>
              <div
                :ref="el => setListInputRef(child.id, el)"
                class="editable-content flex-1"
                contenteditable="true"
                :data-placeholder="placeholder"
                @input="onListInput(child.id, $event)"
                @keydown="onListKeyDown(child.id, childIndex, $event)"
                @focus="onListFocus(child.id)"
                @blur="onBlur"
              ></div>
              <el-button
                size="small"
                link
                class="text-gray-400 hover:text-red-500 ml-2"
              >
                <el-icon @click="removeListItem(childIndex)"><Close /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <div v-else-if="block.type === 'quote'" class="block-quote">
          <div class="quote-header flex items-center mb-2">
            <el-icon class="text-gray-400 mr-2"><ChatDotRound /></el-icon>
            <span class="text-sm font-medium text-gray-600">引用块</span>
            <el-button
              size="small"
              link
              class="ml-auto"
              @click="addQuoteItem"
            >
              + 添加段落
            </el-button>
          </div>
          <div class="quote-content border-l-4 border-blue-300 pl-4 space-y-2 bg-blue-50 p-3 rounded-r-lg">
            <div
              v-for="(child, childIndex) in block.children"
              :key="child.id"
              class="quote-item flex items-start"
            >
              <div
                :ref="el => setQuoteInputRef(child.id, el)"
                class="editable-content flex-1"
                contenteditable="true"
                :data-placeholder="placeholder"
                @input="onQuoteInput(child.id, $event)"
                @keydown="onQuoteKeyDown(child.id, childIndex, $event)"
                @focus="onQuoteFocus(child.id)"
                @blur="onBlur"
              ></div>
              <el-button
                size="small"
                link
                class="text-gray-400 hover:text-red-500 ml-2"
              >
                <el-icon @click="removeQuoteItem(childIndex)"><Close /></el-icon>
              </el-button>
            </div>
            <div v-if="!block.children || block.children.length === 0" class="text-gray-400 text-sm">
              点击上方按钮添加引用内容...
            </div>
          </div>
        </div>

        <div v-else-if="block.type === 'code'" class="block-code">
          <div class="code-header flex items-center justify-between mb-2">
            <span class="text-sm font-medium text-gray-600">代码块</span>
          </div>
          <pre class="bg-gray-900 text-gray-100 p-4 rounded-lg text-sm overflow-x-auto">
            <code
              ref="codeRef"
              class="editable-content"
              contenteditable="true"
              :data-placeholder="placeholder"
              @input="onInput"
              @keydown="onCodeKeyDown"
              @focus="onFocus"
              @blur="onBlur"
            ></code>
          </pre>
        </div>
      </div>

      <div class="block-actions flex flex-col space-y-1">
        <el-tooltip content="删除块">
          <el-button
            size="small"
            link
            class="text-gray-400 hover:text-red-500"
            @click="onDelete"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="转换类型">
          <el-dropdown @command="onTypeChange">
            <el-button
              size="small"
              link
              class="text-gray-400 hover:text-blue-500"
            >
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="paragraph">段落</el-dropdown-item>
                <el-dropdown-item command="bullet-list">无序列表</el-dropdown-item>
                <el-dropdown-item command="numbered-list">有序列表</el-dropdown-item>
                <el-dropdown-item command="quote">引用块</el-dropdown-item>
                <el-dropdown-item command="code">代码块</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { ChatDotRound, Close, Delete, MoreFilled } from '@element-plus/icons-vue'

const props = defineProps({
  block: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    default: 0
  },
  syncVersion: {
    type: Number,
    default: 0
  },
  isDragging: {
    type: Boolean,
    default: false
  },
  isDragOver: {
    type: Boolean,
    default: false
  },
  placeholder: {
    type: String,
    default: '输入内容...'
  }
})

const emit = defineEmits([
  'update',
  'split',
  'delete',
  'keyup',
  'focus',
  'dragstart',
  'dragend',
  'dragover',
  'drop'
])

const inputRef = ref(null)
const codeRef = ref(null)
const listInputRefs = ref({})
const quoteInputRefs = ref({})

const isInitialized = ref(false)
const isUserInputting = ref(false)
const lastKnownContent = ref('')

const getBlockClass = computed(() => {
  return {
    'block-paragraph-type': props.block.type === 'paragraph',
    'block-list-type': props.block.type === 'bullet-list' || props.block.type === 'numbered-list',
    'block-quote-type': props.block.type === 'quote',
    'block-code-type': props.block.type === 'code'
  }
})

const generateId = () => {
  return 'block_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

const setListInputRef = (id, el) => {
  if (el) {
    listInputRefs.value[id] = el
  }
}

const setQuoteInputRef = (id, el) => {
  if (el) {
    quoteInputRefs.value[id] = el
  }
}

const syncContentFromProps = () => {
  if (isUserInputting.value) return
  
  if ((props.block.type === 'paragraph' || props.block.type === 'code')) {
    const targetRef = props.block.type === 'paragraph' ? inputRef.value : codeRef.value
    if (targetRef) {
      const newContent = props.block.content || ''
      if (targetRef.innerHTML !== newContent) {
        targetRef.innerHTML = newContent
        lastKnownContent.value = newContent
      }
    }
  }
  
  if ((props.block.type === 'bullet-list' || props.block.type === 'numbered-list') && props.block.children) {
    props.block.children.forEach(child => {
      const el = listInputRefs.value[child.id]
      if (el) {
        const newContent = child.content || ''
        if (el.innerHTML !== newContent) {
          el.innerHTML = newContent
        }
      }
    })
  }
  
  if (props.block.type === 'quote' && props.block.children) {
    props.block.children.forEach(child => {
      const el = quoteInputRefs.value[child.id]
      if (el) {
        const newContent = child.content || ''
        if (el.innerHTML !== newContent) {
          el.innerHTML = newContent
        }
      }
    })
  }
}

const onInput = (event) => {
  isUserInputting.value = true
  const content = event.target.innerHTML
  lastKnownContent.value = content
  emit('update', props.block.id, { content })
}

const onKeyDown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    const selection = window.getSelection()
    const range = selection.getRangeAt(0)
    
    const beforeRange = document.createRange()
    beforeRange.selectNodeContents(event.target)
    beforeRange.setEnd(range.startContainer, range.startOffset)
    
    const afterRange = document.createRange()
    afterRange.selectNodeContents(event.target)
    afterRange.setStart(range.endContainer, range.endOffset)
    
    const tempDiv1 = document.createElement('div')
    tempDiv1.appendChild(beforeRange.cloneContents())
    const contentBefore = tempDiv1.innerHTML
    
    const tempDiv2 = document.createElement('div')
    tempDiv2.appendChild(afterRange.cloneContents())
    const contentAfter = tempDiv2.innerHTML
    
    emit('split', props.block.id, contentBefore, contentAfter)
  }
  
  if (event.key === 'Backspace') {
    const content = event.target.innerHTML
    if (content === '' || content === '<br>' || content.trim() === '') {
      event.preventDefault()
      emit('delete', props.block.id)
    }
  }
}

const onCodeKeyDown = (event) => {
  if (event.key === 'Tab') {
    event.preventDefault()
    document.execCommand('insertText', false, '  ')
    isUserInputting.value = true
    emit('update', props.block.id, { content: event.target.innerHTML })
  }
}

const onFocus = () => {
  emit('focus')
}

const onListFocus = (childId) => {
  emit('focus')
}

const onQuoteFocus = (childId) => {
  emit('focus')
}

const onBlur = () => {
  isUserInputting.value = false
}

const onDelete = () => {
  emit('delete', props.block.id)
}

const onTypeChange = (type) => {
  emit('update', props.block.id, { type })
}

const onListInput = (childId, event) => {
  isUserInputting.value = true
  const newChildren = props.block.children.map(child => {
    if (child.id === childId) {
      return { ...child, content: event.target.innerHTML }
    }
    return child
  })
  emit('update', props.block.id, { children: newChildren })
}

const onListKeyDown = (childId, childIndex, event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    
    const selection = window.getSelection()
    const range = selection.getRangeAt(0)
    
    const beforeRange = document.createRange()
    beforeRange.selectNodeContents(event.target)
    beforeRange.setEnd(range.startContainer, range.startOffset)
    
    const afterRange = document.createRange()
    afterRange.selectNodeContents(event.target)
    afterRange.setStart(range.endContainer, range.endOffset)
    
    const tempDiv1 = document.createElement('div')
    tempDiv1.appendChild(beforeRange.cloneContents())
    const contentBefore = tempDiv1.innerHTML
    
    const tempDiv2 = document.createElement('div')
    tempDiv2.appendChild(afterRange.cloneContents())
    const contentAfter = tempDiv2.innerHTML
    
    const newChildren = [...props.block.children]
    newChildren[childIndex] = { ...newChildren[childIndex], content: contentBefore }
    
    const newItem = {
      id: generateId(),
      type: 'paragraph',
      content: contentAfter,
      inList: true
    }
    newChildren.splice(childIndex + 1, 0, newItem)
    
    emit('update', props.block.id, { children: newChildren })
  }
  
  if (event.key === 'Backspace') {
    const content = event.target.innerHTML
    if ((content === '' || content === '<br>' || content.trim() === '') && props.block.children.length > 1) {
      event.preventDefault()
      
      const newChildren = [...props.block.children]
      newChildren.splice(childIndex, 1)
      
      emit('update', props.block.id, { children: newChildren })
    }
  }
}

const addListItem = () => {
  const newChildren = [...(props.block.children || [])]
  newChildren.push({
    id: generateId(),
    type: 'paragraph',
    content: '',
    inList: true
  })
  emit('update', props.block.id, { children: newChildren })
}

const removeListItem = (index) => {
  const newChildren = [...props.block.children]
  newChildren.splice(index, 1)
  emit('update', props.block.id, { children: newChildren })
}

const onQuoteInput = (childId, event) => {
  isUserInputting.value = true
  const newChildren = props.block.children.map(child => {
    if (child.id === childId) {
      return { ...child, content: event.target.innerHTML }
    }
    return child
  })
  emit('update', props.block.id, { children: newChildren })
}

const onQuoteKeyDown = (childId, childIndex, event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    
    const selection = window.getSelection()
    const range = selection.getRangeAt(0)
    
    const beforeRange = document.createRange()
    beforeRange.selectNodeContents(event.target)
    beforeRange.setEnd(range.startContainer, range.startOffset)
    
    const afterRange = document.createRange()
    afterRange.selectNodeContents(event.target)
    afterRange.setStart(range.endContainer, range.endOffset)
    
    const tempDiv1 = document.createElement('div')
    tempDiv1.appendChild(beforeRange.cloneContents())
    const contentBefore = tempDiv1.innerHTML
    
    const tempDiv2 = document.createElement('div')
    tempDiv2.appendChild(afterRange.cloneContents())
    const contentAfter = tempDiv2.innerHTML
    
    const newChildren = [...props.block.children]
    newChildren[childIndex] = { ...newChildren[childIndex], content: contentBefore }
    
    const newItem = {
      id: generateId(),
      type: 'paragraph',
      content: contentAfter
    }
    newChildren.splice(childIndex + 1, 0, newItem)
    
    emit('update', props.block.id, { children: newChildren })
  }
  
  if (event.key === 'Backspace') {
    const content = event.target.innerHTML
    if ((content === '' || content === '<br>' || content.trim() === '') && props.block.children.length > 1) {
      event.preventDefault()
      
      const newChildren = [...props.block.children]
      newChildren.splice(childIndex, 1)
      
      emit('update', props.block.id, { children: newChildren })
    }
  }
}

const addQuoteItem = () => {
  const newChildren = [...(props.block.children || [])]
  newChildren.push({
    id: generateId(),
    type: 'paragraph',
    content: ''
  })
  emit('update', props.block.id, { children: newChildren })
}

const removeQuoteItem = (index) => {
  const newChildren = [...props.block.children]
  newChildren.splice(index, 1)
  emit('update', props.block.id, { children: newChildren })
}

const startDrag = () => {
}

const onDragStart = (event) => {
  emit('dragstart')
}

const onDragEnd = () => {
  emit('dragend')
}

const onDragOver = (event) => {
  emit('dragover', event)
}

const onDrop = (event) => {
  emit('drop', event)
}

onMounted(() => {
  nextTick(() => {
    syncContentFromProps()
    isInitialized.value = true
  })
})

watch(() => props.syncVersion, () => {
  if (isInitialized.value) {
    nextTick(() => {
      syncContentFromProps()
    })
  }
})

watch(() => props.block.content, (newContent) => {
  if (isInitialized.value && !isUserInputting.value) {
    nextTick(() => {
      syncContentFromProps()
    })
  }
})

watch(() => props.block.type, () => {
  nextTick(() => {
    syncContentFromProps()
  })
})

watch(() => props.block.children, () => {
  nextTick(() => {
    if (!isUserInputting.value) {
      syncContentFromProps()
    }
  })
}, { deep: true })
</script>

<style scoped>
.block-item-wrapper {
  transition: all 0.2s ease;
  margin-bottom: 8px;
}

.block-item-wrapper.dragging {
  opacity: 0.5;
}

.block-item-wrapper.drag-over {
  border-top: 3px solid #3b82f6;
}

.block-item {
  display: flex;
  align-items: flex-start;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.block-item:hover {
  background-color: #f9fafb;
}

.block-handle {
  cursor: grab;
  padding: 4px;
  margin-right: 8px;
  opacity: 0.5;
  transition: opacity 0.2s ease;
  flex-shrink: 0;
}

.block-handle:hover {
  opacity: 1;
}

.block-handle:active {
  cursor: grabbing;
}

.block-actions {
  opacity: 0;
  transition: opacity 0.2s ease;
  flex-shrink: 0;
}

.block-item:hover .block-actions {
  opacity: 1;
}

.editable-content {
  outline: none;
  min-height: 24px;
  line-height: 1.6;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.editable-content:empty:before {
  content: attr(data-placeholder);
  color: #9ca3af;
  pointer-events: none;
}

.block-list-container .list-item {
  display: flex;
  align-items: flex-start;
}

.block-list-container .list-bullet,
.block-list-container .list-number {
  margin-right: 8px;
  flex-shrink: 0;
  color: #6b7280;
}

.block-quote .quote-item {
  position: relative;
}

.block-code pre {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', monospace;
  margin: 0;
}
</style>
