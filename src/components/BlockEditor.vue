<template>
  <div class="block-editor" ref="editorRef" @paste="handlePaste">
    <div class="flex items-center justify-between mb-3">
      <div class="flex items-center space-x-2">
        <el-button-group size="small">
          <el-button
            :icon="Edit"
            :disabled="!canUndo"
            @click="undo"
          />
          <el-button
            :icon="Plus"
            :disabled="!canRedo"
            @click="redo"
          />
        </el-button-group>
        <el-divider direction="vertical" style="height: 24px" />
        <el-button-group size="small">
          <el-tooltip content="段落">
            <el-button
              :type="activeBlockType === 'paragraph' ? 'primary' : 'default'"
              @click="insertBlock('paragraph')"
            >
              <el-icon><Document /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="无序列表">
            <el-button
              :type="activeBlockType === 'bullet-list' ? 'primary' : 'default'"
              @click="insertBlock('bullet-list')"
            >
              <el-icon><List /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="有序列表">
            <el-button
              :type="activeBlockType === 'numbered-list' ? 'primary' : 'default'"
              @click="insertBlock('numbered-list')"
            >
              <el-icon><DataAnalysis /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="引用块">
            <el-button
              :type="activeBlockType === 'quote' ? 'primary' : 'default'"
              @click="insertBlock('quote')"
            >
              <el-icon><ChatDotRound /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="代码块">
            <el-button
              :type="activeBlockType === 'code' ? 'primary' : 'default'"
              @click="insertBlock('code')"
            >
              <el-icon><Document /></el-icon>
            </el-button>
          </el-tooltip>
        </el-button-group>
      </div>
      <div class="text-xs text-gray-500">
        <span>撤销: {{ history.past.length }} | 重做: {{ history.future.length }}</span>
      </div>
    </div>

    <div class="block-list">
      <BlockItem
        v-for="(block, index) in blocks"
        :key="block.id"
        :block="block"
        :index="index"
        :sync-version="syncVersion"
        :is-dragging="draggedBlockId === block.id"
        :is-drag-over="isDragOver(index)"
        @update="handleBlockUpdate"
        @split="handleBlockSplit"
        @delete="handleBlockDelete"
        @keyup="handleBlockKeyup"
        @focus="handleBlockFocus(block.id)"
        @dragstart="handleDragStart(block.id)"
        @dragend="handleDragEnd"
        @dragover="handleDragOver($event, index)"
        @drop="handleDrop($event, index)"
      />
    </div>

    <div class="mt-3">
      <el-button 
        type="primary" 
        link 
        size="small" 
        @click="addNewBlockAtEnd"
      >
        + 添加新块
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { 
  Plus, Edit, Document, List, ChatDotRound, DataAnalysis 
} from '@element-plus/icons-vue'
import BlockItem from './BlockItem.vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '开始输入内容...'
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const editorRef = ref(null)
const blocks = ref([])
const draggedBlockId = ref(null)
const dragOverIndex = ref(-1)
const focusedBlockId = ref(null)
const syncVersion = ref(0)

const history = ref({
  past: [],
  future: [],
  maxSize: 50
})

const canUndo = computed(() => history.value.past.length > 0)
const canRedo = computed(() => history.value.future.length > 0)

const activeBlockType = computed(() => {
  if (!focusedBlockId.value) return null
  const block = findBlockById(blocks.value, focusedBlockId.value)
  return block ? block.type : null
})

const generateId = () => {
  return 'block_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

const createBlock = (type = 'paragraph', content = '', children = []) => ({
  id: generateId(),
  type,
  content,
  children: type === 'quote' || type === 'bullet-list' || type === 'numbered-list' 
    ? children 
    : []
})

const findBlockById = (blockList, id) => {
  for (const block of blockList) {
    if (block.id === id) return block
    if (block.children && block.children.length > 0) {
      const found = findBlockById(block.children, id)
      if (found) return found
    }
  }
  return null
}

const updateBlockInList = (blockList, id, updates) => {
  return blockList.map(block => {
    if (block.id === id) {
      return { ...block, ...updates }
    }
    if (block.children && block.children.length > 0) {
      return {
        ...block,
        children: updateBlockInList(block.children, id, updates)
      }
    }
    return block
  })
}

const saveToHistory = () => {
  const snapshot = JSON.parse(JSON.stringify(blocks.value))
  if (history.value.past.length >= history.value.maxSize) {
    history.value.past.shift()
  }
  history.value.past.push(snapshot)
  history.value.future = []
}

const undo = () => {
  if (!canUndo.value) return
  
  const current = JSON.parse(JSON.stringify(blocks.value))
  history.value.future.unshift(current)
  
  const previous = history.value.past.pop()
  blocks.value = previous
  syncVersion.value++
  emitUpdate()
}

const redo = () => {
  if (!canRedo.value) return
  
  const current = JSON.parse(JSON.stringify(blocks.value))
  history.value.past.push(current)
  
  const next = history.value.future.shift()
  blocks.value = next
  syncVersion.value++
  emitUpdate()
}

const emitUpdate = () => {
  emit('update:modelValue', blocks.value)
  emit('change', blocks.value)
}

const handleBlockUpdate = (blockId, updates) => {
  saveToHistory()
  blocks.value = updateBlockInList(blocks.value, blockId, updates)
  emitUpdate()
}

const handleBlockSplit = (blockId, contentBefore, contentAfter) => {
  saveToHistory()
  
  const newList = []
  for (let i = 0; i < blocks.value.length; i++) {
    const block = blocks.value[i]
    if (block.id === blockId) {
      newList.push({ ...block, content: contentBefore })
      const newBlock = createBlock(block.type, contentAfter)
      newList.push(newBlock)
    } else {
      newList.push(block)
    }
  }
  blocks.value = newList
  emitUpdate()
}

const handleBlockDelete = (blockId) => {
  if (blocks.value.length <= 1) return
  
  saveToHistory()
  
  const filterBlocks = (blockList) => {
    return blockList.filter(block => {
      if (block.id === blockId) return false
      if (block.children && block.children.length > 0) {
        block.children = filterBlocks(block.children)
      }
      return true
    })
  }
  
  blocks.value = filterBlocks(blocks.value)
  emitUpdate()
}

const handleBlockKeyup = (event, blockId) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
  }
}

const handleBlockFocus = (blockId) => {
  focusedBlockId.value = blockId
}

const insertBlock = (type) => {
  saveToHistory()
  
  const newBlock = createBlock(type)
  
  if (focusedBlockId.value) {
    const index = blocks.value.findIndex(b => b.id === focusedBlockId.value)
    if (index !== -1) {
      blocks.value.splice(index + 1, 0, newBlock)
    } else {
      blocks.value.push(newBlock)
    }
  } else {
    blocks.value.push(newBlock)
  }
  
  emitUpdate()
}

const addNewBlockAtEnd = () => {
  saveToHistory()
  blocks.value.push(createBlock('paragraph'))
  emitUpdate()
}

const handleDragStart = (blockId) => {
  draggedBlockId.value = blockId
}

const handleDragEnd = () => {
  draggedBlockId.value = null
  dragOverIndex.value = -1
}

const handleDragOver = (event, index) => {
  event.preventDefault()
  dragOverIndex.value = index
}

const handleDrop = (event, toIndex) => {
  event.preventDefault()
  
  if (!draggedBlockId.value || dragOverIndex.value === -1) return
  
  const fromIndex = blocks.value.findIndex(b => b.id === draggedBlockId.value)
  if (fromIndex === toIndex) return
  
  saveToHistory()
  
  const newBlocks = [...blocks.value]
  const [draggedBlock] = newBlocks.splice(fromIndex, 1)
  
  let actualToIndex = toIndex
  if (fromIndex < toIndex) {
    actualToIndex = toIndex - 1
  }
  
  newBlocks.splice(actualToIndex, 0, draggedBlock)
  blocks.value = newBlocks
  
  draggedBlockId.value = null
  dragOverIndex.value = -1
  
  emitUpdate()
}

const isDragOver = (index) => {
  return dragOverIndex.value === index && draggedBlockId.value && 
         blocks.value.findIndex(b => b.id === draggedBlockId.value) !== index
}

const sanitizeHTML = (html) => {
  const parser = new DOMParser()
  const doc = parser.parseFromString(html, 'text/html')
  
  const allowedTags = ['p', 'ul', 'ol', 'li', 'blockquote', 'code', 'pre', 'br', 'strong', 'b', 'em', 'i', 'u', 'span', 'div']
  const allowedAttributes = ['class', 'style']
  
  const sanitizeElement = (element) => {
    if (element.nodeType === Node.TEXT_NODE) {
      return element.textContent
    }
    
    if (element.nodeType !== Node.ELEMENT_NODE) {
      return ''
    }
    
    const tagName = element.tagName.toLowerCase()
    
    if (!allowedTags.includes(tagName)) {
      let content = ''
      for (const child of element.childNodes) {
        content += sanitizeElement(child)
      }
      return content
    }
    
    let attrs = ''
    for (const attr of element.attributes) {
      if (allowedAttributes.includes(attr.name.toLowerCase())) {
        attrs += ` ${attr.name}="${attr.value}"`
      }
    }
    
    let content = ''
    for (const child of element.childNodes) {
      content += sanitizeElement(child)
    }
    
    return `<${tagName}${attrs}>${content}</${tagName}>`
  }
  
  return sanitizeElement(doc.body)
}

const htmlToBlocks = (html) => {
  const blocks = []
  const parser = new DOMParser()
  const doc = parser.parseFromString(html, 'text/html')
  
  const processNode = (node) => {
    if (node.nodeType === Node.TEXT_NODE) {
      const text = node.textContent.trim()
      if (text) {
        blocks.push(createBlock('paragraph', text))
      }
      return
    }
    
    if (node.nodeType !== Node.ELEMENT_NODE) return
    
    const tagName = node.tagName.toLowerCase()
    
    switch (tagName) {
      case 'blockquote':
        const quoteBlock = createBlock('quote', '')
        const quoteContent = []
        for (const child of node.children) {
          const childBlocks = htmlToBlocks(child.outerHTML)
          quoteContent.push(...childBlocks)
        }
        if (quoteContent.length > 0) {
          quoteBlock.children = quoteContent
        }
        blocks.push(quoteBlock)
        break
        
      case 'ul':
        const bulletList = createBlock('bullet-list', '')
        bulletList.children = []
        for (const li of node.children) {
          if (li.tagName.toLowerCase() === 'li') {
            const liBlocks = htmlToBlocks(li.innerHTML)
            if (liBlocks.length > 0) {
              bulletList.children.push(...liBlocks.map(b => ({ ...b, inList: true })))
            }
          }
        }
        if (bulletList.children.length > 0) {
          blocks.push(bulletList)
        }
        break
        
      case 'ol':
        const numberedList = createBlock('numbered-list', '')
        numberedList.children = []
        for (const li of node.children) {
          if (li.tagName.toLowerCase() === 'li') {
            const liBlocks = htmlToBlocks(li.innerHTML)
            if (liBlocks.length > 0) {
              numberedList.children.push(...liBlocks.map(b => ({ ...b, inList: true })))
            }
          }
        }
        if (numberedList.children.length > 0) {
          blocks.push(numberedList)
        }
        break
        
      case 'pre':
      case 'code':
        blocks.push(createBlock('code', node.textContent))
        break
        
      case 'p':
      case 'div':
      case 'section':
      case 'article':
        const text = node.textContent.trim()
        if (text) {
          blocks.push(createBlock('paragraph', text))
        }
        for (const child of node.children) {
          if (!['strong', 'b', 'em', 'i', 'u', 'span', 'br'].includes(child.tagName.toLowerCase())) {
            processNode(child)
          }
        }
        break
        
      case 'br':
        break
        
      default:
        for (const child of node.childNodes) {
          processNode(child)
        }
    }
  }
  
  for (const child of doc.body.childNodes) {
    processNode(child)
  }
  
  return blocks
}

const handlePaste = (event) => {
  event.preventDefault()
  
  const clipboardData = event.clipboardData || window.clipboardData
  const html = clipboardData.getData('text/html')
  const text = clipboardData.getData('text/plain')
  
  saveToHistory()
  
  if (html) {
    const sanitized = sanitizeHTML(html)
    const newBlocks = htmlToBlocks(sanitized)
    
    if (newBlocks.length > 0) {
      if (focusedBlockId.value) {
        const index = blocks.value.findIndex(b => b.id === focusedBlockId.value)
        if (index !== -1) {
          blocks.value.splice(index + 1, 0, ...newBlocks)
        } else {
          blocks.value.push(...newBlocks)
        }
      } else {
        blocks.value.push(...newBlocks)
      }
    }
  } else if (text) {
    const lines = text.split('\n')
    const newBlocks = lines.filter(line => line.trim()).map(line => createBlock('paragraph', line.trim()))
    
    if (newBlocks.length > 0) {
      if (focusedBlockId.value) {
        const index = blocks.value.findIndex(b => b.id === focusedBlockId.value)
        if (index !== -1) {
          blocks.value.splice(index + 1, 0, ...newBlocks)
        } else {
          blocks.value.push(...newBlocks)
        }
      } else {
        blocks.value.push(...newBlocks)
      }
    }
  }
  
  emitUpdate()
}

const initializeBlocks = () => {
  if (props.modelValue && props.modelValue.length > 0) {
    blocks.value = JSON.parse(JSON.stringify(props.modelValue))
  } else {
    blocks.value = [createBlock('paragraph')]
  }
}

watch(() => props.modelValue, (newVal) => {
  if (JSON.stringify(newVal) !== JSON.stringify(blocks.value)) {
    blocks.value = JSON.parse(JSON.stringify(newVal || [createBlock('paragraph')]))
  }
}, { deep: true })

onMounted(() => {
  initializeBlocks()
})
</script>

<style scoped>
.block-editor {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  background-color: #fff;
}

.block-list {
  min-height: 100px;
}
</style>
