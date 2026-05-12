<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-800">块状富文本编辑器演示</h2>
    </div>
    
    <el-card>
      <div class="mb-4">
        <h3 class="text-lg font-semibold mb-2">编辑器演示</h3>
        <BlockEditor 
          v-model="editorContent" 
          @change="onEditorChange"
        />
      </div>
      
      <div class="border-t pt-4">
        <h3 class="text-lg font-semibold mb-2">内容预览（JSON）</h3>
        <pre class="bg-gray-100 p-4 rounded-lg text-sm overflow-x-auto">{{ JSON.stringify(editorContent, null, 2) }}</pre>
      </div>
    </el-card>
    
    <el-card>
      <h3 class="text-lg font-semibold mb-4">功能说明</h3>
      <div class="space-y-3 text-sm text-gray-600">
        <div class="flex items-start space-x-2">
          <span class="text-blue-500 font-medium">•</span>
          <div>
            <strong>多种块类型：</strong>支持段落、无序列表、有序列表、引用块、代码块
          </div>
        </div>
        <div class="flex items-start space-x-2">
          <span class="text-blue-500 font-medium">•</span>
          <div>
            <strong>嵌套结构：</strong>列表和引用块支持嵌套内容，可以添加/删除子项
          </div>
        </div>
        <div class="flex items-start space-x-2">
          <span class="text-blue-500 font-medium">•</span>
          <div>
            <strong>拖拽排序：</strong>通过左侧的拖拽手柄调整块的顺序
          </div>
        </div>
        <div class="flex items-start space-x-2">
          <span class="text-blue-500 font-medium">•</span>
          <div>
            <strong>撤销/重做：</strong>支持块级别的撤销（最多50步）和重做操作
          </div>
        </div>
        <div class="flex items-start space-x-2">
          <span class="text-blue-500 font-medium">•</span>
          <div>
            <strong>HTML 清洗：</strong>粘贴内容时会自动清理不安全的 HTML 标签
          </div>
        </div>
        <div class="flex items-start space-x-2">
          <span class="text-blue-500 font-medium">•</span>
          <div>
            <strong>快捷操作：</strong>
            <ul class="list-disc list-inside mt-1 space-y-1">
              <li><kbd class="px-1 py-0.5 bg-gray-200 rounded text-xs">Enter</kbd> 在当前位置拆分块，创建新块</li>
              <li><kbd class="px-1 py-0.5 bg-gray-200 rounded text-xs">Shift + Enter</kbd> 仅换行，不拆分块</li>
              <li><kbd class="px-1 py-0.5 bg-gray-200 rounded text-xs">Backspace</kbd> 在空块上删除该块</li>
              <li><kbd class="px-1 py-0.5 bg-gray-200 rounded text-xs">Tab</kbd> 在代码块中插入两个空格</li>
            </ul>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import BlockEditor from '../components/BlockEditor.vue'

const editorContent = ref([
  {
    id: 'block_demo_1',
    type: 'paragraph',
    content: '这是一个<strong>块状富文本编辑器</strong>的演示。你可以在下方输入内容，使用工具栏添加不同类型的块。',
    children: []
  },
  {
    id: 'block_demo_2',
    type: 'quote',
    content: '',
    children: [
      {
        id: 'block_demo_quote_1',
        type: 'paragraph',
        content: '好的代码是写出来给人看的，附带能在机器上运行。',
        children: []
      },
      {
        id: 'block_demo_quote_2',
        type: 'paragraph',
        content: '—— Martin Fowler',
        children: []
      }
    ]
  },
  {
    id: 'block_demo_3',
    type: 'bullet-list',
    content: '',
    children: [
      {
        id: 'block_demo_list_1',
        type: 'paragraph',
        content: '支持多种块类型',
        inList: true,
        children: []
      },
      {
        id: 'block_demo_list_2',
        type: 'paragraph',
        content: '拖拽调整顺序',
        inList: true,
        children: []
      },
      {
        id: 'block_demo_list_3',
        type: 'paragraph',
        content: '撤销和重做',
        inList: true,
        children: []
      }
    ]
  },
  {
    id: 'block_demo_4',
    type: 'code',
    content: 'function hello() {\n  console.log("Hello, World!");\n}\n\nhello();',
    children: []
  }
])

const onEditorChange = (content) => {
  console.log('Editor content changed:', content)
}
</script>
