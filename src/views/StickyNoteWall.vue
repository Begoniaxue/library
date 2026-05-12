<template>
  <div class="sticky-note-wall-container">
    <div class="toolbar">
      <h2 class="text-lg font-semibold text-gray-700">便签墙 - 多人实时协作</h2>
      <div class="connection-status" :class="{ connected: isConnected }">
        <span class="dot"></span>
        <span>{{ isConnected ? '已连接' : '未连接' }}</span>
      </div>
    </div>
    
    <div 
      ref="canvasRef" 
      class="canvas"
      @click="handleCanvasClick"
      @mousedown="handleCanvasMouseDown"
      @mousemove="handleCanvasMouseMove"
      @mouseup="handleCanvasMouseUp"
      @wheel="handleCanvasWheel"
      :style="canvasStyle"
    >
      <div class="canvas-content" :style="transformStyle">
        <div
          v-for="note in stickyNotes"
          :key="note.id"
          class="sticky-note"
          :class="{ 'editing': editingId === note.id, 'dragging': draggingId === note.id }"
          :style="getNoteStyle(note)"
          @mousedown.stop="handleNoteMouseDown($event, note)"
          @dblclick.stop="startEditing(note)"
          @contextmenu.prevent="showDeleteMenu($event, note)"
        >
          <div class="note-header">
            <div class="note-color" :style="{ backgroundColor: getNoteColor(note) }"></div>
            <button class="delete-btn" @click.stop="deleteNote(note.id)" title="删除">×</button>
          </div>
          
          <div v-if="editingId === note.id" class="note-editor">
            <textarea
              ref="editorRef"
              v-model="editingContent"
              @blur="saveContent(note.id)"
              @keydown.enter.ctrl="saveContent(note.id)"
              @click.stop
            ></textarea>
            <div class="editor-tip">按 Ctrl+Enter 保存，点击外部取消</div>
          </div>
          
          <div v-else class="note-content" @click.stop>
            <span v-if="note.content">{{ note.content }}</span>
            <span v-else class="placeholder">双击编辑</span>
          </div>
        </div>
      </div>
      
      <div class="canvas-hint" v-if="stickyNotes.length === 0 && !loading">
        <p>点击画布任意位置创建便签</p>
        <p class="hint-detail">双击便签编辑内容，拖拽便签调整位置</p>
      </div>
      
      <div class="zoom-controls">
        <button @click="zoomIn" title="放大">+</button>
        <button @click="zoomReset" title="重置">{{ Math.round(scale * 100) }}%</button>
        <button @click="zoomOut" title="缩小">−</button>
      </div>
      
      <div v-if="loading" class="loading-overlay">
        <div class="loading-spinner"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { Client } from '@stomp/stompjs'
import { stickyNoteService } from '../services/api'

const canvasRef = ref(null)
const editorRef = ref(null)

const stickyNotes = ref([])
const loading = ref(true)
const isConnected = ref(false)

const scale = ref(1)
const offsetX = ref(0)
const offsetY = ref(0)

const isPanning = ref(false)
const panStartX = ref(0)
const panStartY = ref(0)
const panStartOffsetX = ref(0)
const panStartOffsetY = ref(0)

const draggingId = ref(null)
const dragOffsetX = ref(0)
const dragOffsetY = ref(0)

const editingId = ref(null)
const editingContent = ref('')

let stompClient = null
let subscriptions = []

const canvasStyle = computed(() => ({
  transform: `scale(${scale.value})`,
  transformOrigin: '0 0'
}))

const transformStyle = computed(() => ({
  transform: `translate(${offsetX.value}px, ${offsetY.value}px)`
}))

const getNoteStyle = (note) => ({
  left: `${note.x}px`,
  top: `${note.y}px`
})

const getNoteColor = (note) => {
  const colors = ['#FFD700', '#FFB6C1', '#98FB98', '#87CEEB', '#DDA0DD']
  const index = Math.abs(note.id.charCodeAt(0)) % colors.length
  return colors[index]
}

const loadStickyNotes = async () => {
  loading.value = true
  try {
    const notes = await stickyNoteService.getAll()
    stickyNotes.value = notes || []
  } catch (error) {
    console.error('Failed to load sticky notes:', error)
    stickyNotes.value = []
  } finally {
    loading.value = false
  }
}

const connectWebSocket = () => {
  if (stompClient && stompClient.connected) {
    return
  }
  
  const wsUrl = window.location.protocol === 'https:' 
    ? `wss://${window.location.host}/ws/sticky-notes/websocket`
    : `ws://${window.location.host}/ws/sticky-notes/websocket`
  
  stompClient = new Client({
    brokerURL: wsUrl,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    debug: (str) => {
    }
  })
  
  stompClient.onConnect = (frame) => {
    isConnected.value = true
    console.log('WebSocket connected')
    
    subscriptions = []
    
    subscriptions.push(stompClient.subscribe('/topic/sticky-notes/create', (message) => {
      const body = JSON.parse(message.body)
      if (!stickyNotes.value.find(n => n.id === body.id)) {
        stickyNotes.value.push(body)
      }
    }))
    
    subscriptions.push(stompClient.subscribe('/topic/sticky-notes/move', (message) => {
      const body = JSON.parse(message.body)
      if (draggingId.value !== body.id) {
        const note = stickyNotes.value.find(n => n.id === body.id)
        if (note) {
          note.x = body.x
          note.y = body.y
        }
      }
    }))
    
    subscriptions.push(stompClient.subscribe('/topic/sticky-notes/update', (message) => {
      const body = JSON.parse(message.body)
      const updateNote = stickyNotes.value.find(n => n.id === body.id)
      if (updateNote) {
        updateNote.content = body.content
      }
    }))
    
    subscriptions.push(stompClient.subscribe('/topic/sticky-notes/delete', (message) => {
      const id = JSON.parse(message.body)
      stickyNotes.value = stickyNotes.value.filter(n => n.id !== id)
      if (editingId.value === id) {
        editingId.value = null
        editingContent.value = ''
      }
    }))
  }
  
  stompClient.onDisconnect = (frame) => {
    isConnected.value = false
    console.log('WebSocket disconnected')
  }
  
  stompClient.onStompError = (frame) => {
    console.error('STOMP error:', frame.headers['message'])
    isConnected.value = false
  }
  
  stompClient.activate()
}

const sendWebSocketMessage = (destination, body) => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: destination,
      body: JSON.stringify(body)
    })
  }
}

const handleCanvasClick = async (event) => {
  if (draggingId.value || isPanning.value) return
  if (editingId.value) {
    saveContent(editingId.value)
    return
  }
  
  const rect = canvasRef.value.getBoundingClientRect()
  const x = (event.clientX - rect.left) / scale.value - offsetX.value
  const y = (event.clientY - rect.top) / scale.value - offsetY.value
  
  try {
    const newNote = await stickyNoteService.create(x, y, '')
    if (!stickyNotes.value.find(n => n.id === newNote.id)) {
      stickyNotes.value.push(newNote)
    }
    nextTick(() => {
      const createdNote = stickyNotes.value.find(n => n.id === newNote.id)
      if (createdNote) {
        startEditing(createdNote)
      }
    })
  } catch (error) {
    console.error('Failed to create sticky note:', error)
  }
}

const handleCanvasMouseDown = (event) => {
  if (event.button !== 0) return
  
  isPanning.value = true
  panStartX.value = event.clientX
  panStartY.value = event.clientY
  panStartOffsetX.value = offsetX.value
  panStartOffsetY.value = offsetY.value
}

let lastMoveTime = 0

const handleCanvasMouseMove = (event) => {
  if (draggingId.value) {
    const note = stickyNotes.value.find(n => n.id === draggingId.value)
    if (note) {
      const rect = canvasRef.value.getBoundingClientRect()
      const x = (event.clientX - rect.left) / scale.value - dragOffsetX.value - offsetX.value
      const y = (event.clientY - rect.top) / scale.value - dragOffsetY.value - offsetY.value
      note.x = x
      note.y = y
      
      const now = Date.now()
      if (now - lastMoveTime > 30) {
        lastMoveTime = now
        sendWebSocketMessage('/app/sticky-notes/move', {
          id: note.id,
          x: note.x,
          y: note.y
        })
      }
    }
    return
  }
  
  if (isPanning.value) {
    const deltaX = (event.clientX - panStartX.value) / scale.value
    const deltaY = (event.clientY - panStartY.value) / scale.value
    offsetX.value = panStartOffsetX.value + deltaX
    offsetY.value = panStartOffsetY.value + deltaY
  }
}

const handleCanvasMouseUp = () => {
  if (draggingId.value) {
    const note = stickyNotes.value.find(n => n.id === draggingId.value)
    if (note) {
      stickyNoteService.updatePosition(note.id, note.x, note.y).catch(err => {
        console.error('Failed to update note position:', err)
      })
    }
  }
  
  draggingId.value = null
  isPanning.value = false
}

const handleNoteMouseDown = (event, note) => {
  if (editingId.value === note.id) return
  
  draggingId.value = note.id
  const rect = event.currentTarget.getBoundingClientRect()
  dragOffsetX.value = event.clientX - rect.left
  dragOffsetY.value = event.clientY - rect.top
}

const handleCanvasWheel = (event) => {
  event.preventDefault()
  
  const delta = event.deltaY > 0 ? 0.9 : 1.1
  const newScale = Math.max(0.25, Math.min(2, scale.value * delta))
  
  const rect = canvasRef.value.getBoundingClientRect()
  const mouseX = (event.clientX - rect.left) / scale.value
  const mouseY = (event.clientY - rect.top) / scale.value
  
  scale.value = newScale
  
  offsetX.value = offsetX.value - mouseX * (1 - delta)
  offsetY.value = offsetY.value - mouseY * (1 - delta)
}

const startEditing = (note) => {
  editingId.value = note.id
  editingContent.value = note.content || ''
  nextTick(() => {
    if (editorRef.value) {
      editorRef.value.focus()
      editorRef.value.select()
    }
  })
}

const saveContent = async (id) => {
  if (!editingId.value) return
  
  const note = stickyNotes.value.find(n => n.id === id)
  if (note && note.content !== editingContent.value) {
    try {
      await stickyNoteService.updateContent(id, editingContent.value)
      note.content = editingContent.value
    } catch (error) {
      console.error('Failed to update note content:', error)
    }
  }
  
  editingId.value = null
  editingContent.value = ''
}

const deleteNote = async (id) => {
  try {
    await stickyNoteService.delete(id)
    stickyNotes.value = stickyNotes.value.filter(n => n.id !== id)
    if (editingId.value === id) {
      editingId.value = null
      editingContent.value = ''
    }
  } catch (error) {
    console.error('Failed to delete note:', error)
  }
}

const showDeleteMenu = (event, note) => {
  if (confirm('确定要删除这个便签吗？')) {
    deleteNote(note.id)
  }
}

const zoomIn = () => {
  scale.value = Math.min(2, scale.value * 1.2)
}

const zoomOut = () => {
  scale.value = Math.max(0.25, scale.value * 0.8)
}

const zoomReset = () => {
  scale.value = 1
  offsetX.value = 0
  offsetY.value = 0
}

onMounted(() => {
  loadStickyNotes()
  connectWebSocket()
})

onUnmounted(() => {
  if (subscriptions) {
    subscriptions.forEach(sub => {
      try {
        sub.unsubscribe()
      } catch (e) {
      }
    })
    subscriptions = []
  }
  if (stompClient) {
    try {
      stompClient.deactivate()
    } catch (e) {
    }
    stompClient = null
  }
})
</script>

<style scoped>
.sticky-note-wall-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  background: #fee2e2;
  color: #dc2626;
  font-size: 14px;
}

.connection-status.connected {
  background: #d1fae5;
  color: #059669;
}

.connection-status .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #dc2626;
}

.connection-status.connected .dot {
  background: #059669;
}

.canvas {
  flex: 1;
  position: relative;
  overflow: hidden;
  background: #f8fafc;
  background-image: 
    linear-gradient(rgba(0,0,0,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0,0,0,0.03) 1px, transparent 1px);
  background-size: 20px 20px;
  cursor: grab;
}

.canvas:active {
  cursor: grabbing;
}

.canvas-content {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.sticky-note {
  position: absolute;
  width: 200px;
  min-height: 150px;
  background: linear-gradient(135deg, #fff9c4 0%, #fff59d 100%);
  border-radius: 4px;
  box-shadow: 2px 2px 8px rgba(0,0,0,0.15);
  cursor: move;
  user-select: none;
  transition: box-shadow 0.2s, transform 0.1s;
  display: flex;
  flex-direction: column;
}

.sticky-note:hover {
  box-shadow: 4px 4px 12px rgba(0,0,0,0.2);
}

.sticky-note.dragging {
  z-index: 1000;
  box-shadow: 6px 6px 16px rgba(0,0,0,0.3);
}

.sticky-note.editing {
  z-index: 1001;
  box-shadow: 6px 6px 16px rgba(0,0,0,0.3);
}

.note-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
  border-bottom: 1px solid rgba(0,0,0,0.05);
  border-radius: 4px 4px 0 0;
}

.note-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  opacity: 0.8;
}

.delete-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  line-height: 1;
  padding: 0 4px;
  transition: color 0.2s;
}

.delete-btn:hover {
  color: #ef4444;
}

.note-content {
  flex: 1;
  padding: 12px;
  font-size: 14px;
  line-height: 1.5;
  color: #374151;
  white-space: pre-wrap;
  word-break: break-word;
  overflow: auto;
}

.note-content .placeholder {
  color: #9ca3af;
  font-style: italic;
}

.note-editor {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.note-editor textarea {
  flex: 1;
  width: 100%;
  padding: 12px;
  border: none;
  background: transparent;
  font-size: 14px;
  line-height: 1.5;
  color: #374151;
  resize: none;
  outline: none;
  font-family: inherit;
}

.editor-tip {
  padding: 4px 12px 8px;
  font-size: 11px;
  color: #9ca3af;
}

.canvas-hint {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #9ca3af;
  pointer-events: none;
}

.canvas-hint p:first-child {
  font-size: 18px;
  margin-bottom: 8px;
}

.canvas-hint .hint-detail {
  font-size: 14px;
}

.zoom-controls {
  position: absolute;
  bottom: 16px;
  right: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow: hidden;
}

.zoom-controls button {
  width: 40px;
  height: 40px;
  border: none;
  background: #fff;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s;
}

.zoom-controls button:hover {
  background: #f3f4f6;
}

.zoom-controls button:nth-child(2) {
  font-size: 12px;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255,255,255,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
