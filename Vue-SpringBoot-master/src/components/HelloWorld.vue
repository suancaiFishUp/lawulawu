<template>
  <div class="container">
    <!-- Header Container with Username and Upload Button -->
    <div class="header-container">
      <div class="user-info">
        <span class="username">{{ username }}</span>
        <button class="upload-button-top" @click="showUploadDialog">上传图片</button>
      </div>

      <!-- Search Bar Positioned in the Center -->
      <div class="search-container-father">
        <div class="search-container">
          <input
            v-model="note"
            @keyup.enter="fetchImages"
            class="search-input"
            placeholder="输入图片描述..."
          />
          <button @click="fetchImages" class="search-button">搜索</button>
        </div>
      </div>
    </div>

    <!-- Display Images -->
    <div class="image-container">
      <div class="image-column" v-for="(column, index) in imageColumns" :key="index">
        <div v-for="image in column" :key="image.id" class="image-item">
          <img :src="image.url" :alt="image.name" @click="enlargeImage(image)" />
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <!-- Image Enlarging Modal -->
    <div v-if="enlargedImage" class="image-modal" @click="enlargedImage = null">
      <div class="modal-content">
        <img :src="enlargedImage.url" alt="Enlarged Image" />
        <div class="image-note">{{ enlargedImage.note }}</div> <!-- Display image note here -->
      </div>
    </div>

    <!-- Upload Image Dialog -->
    <div v-if="showUpload" class="upload-dialog">
      <div class="dialog-overlay" @click="showUpload = false"></div>
      <div class="dialog-content">
        <span class="close-button" @click="showUpload = false">&times;</span>
        <h2>上传图片</h2>
        <input type="file" @change="onFileChange" accept="image/*" class="file-input" />
        <input type="text" v-model="imageDescription" placeholder="输入图片描述..." class="input-text" />
        <button @click="uploadImage" class="upload-button">上传图片</button>
      </div>
    </div>

    <!-- Infinite Scroll Target -->
    <div class="infinite-scroll-target"></div>
  </div>
</template>


<script>
import http from '@/http/http.js';

export default {
  data() {
    return {
      imageColumns: [],
      columnHeights: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      loading: false,
      note: '',
      imageFile: null,
      imageDescription: '',
      showUpload: false,
      username: '',
      observer: null,
      enlargedImage: null,
    };
  },
  methods: {
    getColumnCount() {
      const width = window.innerWidth;
      if (width >= 1024) return 4; // Desktop
      else if (width >= 768) return 3; // Tablet
      else return 2; // Mobile
    },

    initializeColumns() {
      const columnCount = this.getColumnCount();
      this.imageColumns = Array.from({ length: columnCount }, () => []);
      this.columnHeights = Array.from({ length: columnCount }, () => 0);
    },

    async fetchImages() {
      this.initializeColumns();
      this.page = 1;
      this.hasMore = true;
      if (this.observer) this.observer.disconnect();
      await this.loadPage();
      this.setupInfiniteScroll();
    },

    async loadPage() {
      if (!this.hasMore || this.loading) return;
      this.loading = true;
      try {
        const response = await http.post('/person/restResult', {
          page: this.page,
          pageSize: this.pageSize,
          note: this.note,
        });
        const { pictureList, hasMore } = response.data.data;
        this.hasMore = hasMore;

        pictureList.forEach((image) => {
          const minIndex = this.columnHeights.indexOf(Math.min(...this.columnHeights));
          this.imageColumns[minIndex].push(image);
          this.columnHeights[minIndex] += image.height || 1; // Use a default height if not provided
        });

        this.page += 1;
      } catch (error) {
        console.error('获取图片失败:', error);
      }
      this.loading = false;
    },

    setupInfiniteScroll() {
      this.observer = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              this.loadPage();
            }
          });
        },
        { rootMargin: '200px' } // 提前加载，用户体验更好
      );

      const target = this.$el.querySelector('.infinite-scroll-target');
      if (target) {
        this.observer.observe(target);
      }
    },

    onFileChange(e) {
      this.imageFile = e.target.files[0];
    },

    async uploadImage() {
      if (!this.imageFile) {
        alert('请选取要上传的图片!');
        return;
      }

      // 检查文件大小 (例如：限制为5MB)
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (this.imageFile.size > maxSize) {
        alert('文件过大，最大允许5MB');
        return;
      }

      const formData = new FormData();
      formData.append('file', this.imageFile);
      formData.append('description', this.imageDescription);

      try {
        const response = await http.post('/person/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });

        alert('上传成功!');
        this.showUpload = false;
        this.imageFile = null;
        this.imageDescription = '';
        this.fetchImages();
      } catch (error) {
        console.error('上传图片失败:', error);
        alert('上传图片失败，请重试。'); // 提供用户反馈
      }
    },

    showUploadDialog() {
      this.showUpload = true;
    },

    async getUsername() {
      try {
        const response = await http.get('/person/getUsername');
        this.username = response.data.message;
      } catch (error) {
        console.error('获取用户名失败:', error);
      }
    },

    handleResize() {
      this.fetchImages();
    },

    enlargeImage(image) {
      this.enlargedImage = image;
    },
  },

  mounted() {
    this.getUsername();
    this.fetchImages();
    window.addEventListener('resize', this.handleResize);
  },

  beforeDestroy() {
    if (this.observer) this.observer.disconnect();
    window.removeEventListener('resize', this.handleResize);
  },
};
</script>

<style scoped>
.container {
  padding: 20px;
}

/* Header Styles */
.header-container {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
}

.username {
  margin-right: 20px;
  font-size: 16px;
  color: #333;
}

.upload-button-top {
  padding: 8px 16px;
  background-color: #007bff;
  border-radius: 50px;
  color: white;
  border: none;
  cursor: pointer;
  margin-right: 10px;
}

.upload-button-top:hover {
  background-color: #0056b3;
}

/* Search Bar Styles */
.search-container-father {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
}

.search-container {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  padding: 10px 20px;
  border-radius: 50px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 600px;
}

.search-input {
  flex: 1;
  padding: 15px 13px;
  border: none;
  border-radius: 50px;
  font-size: 16px;
  outline: none;
  background-color: #fff;
  margin-right: 10px;
}

.search-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-size: 16px;
}

.search-button:hover {
  background-color: #0056b3;
}

/* Image Grid Styles */
.image-container {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.image-column {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.image-item img {
  width: 100%;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* Loading Indicator */
.loading {
  text-align: center;
  margin: 20px 0;
  font-size: 18px;
  color: #555;
}

/* Upload Dialog Styles */
.upload-dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.dialog-content {
  position: relative;
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  z-index: 1001;
  width: 70%;
  max-width: 400px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.close-button {
  position: absolute;
  top: 10px;
  right: 15px;
  font-size: 24px;
  color: #aaa;
  cursor: pointer;
}

.close-button:hover {
  color: #000;
}

.file-input,
.input-text {
  width: 90%;
  padding: 10px 15px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  outline: none;
}

.upload-button {
  width: 100%;
  padding: 10px 0;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 18px;
  cursor: pointer;
}

.upload-button:hover {
  background-color: #0056b3;
}

.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1002;
  cursor: zoom-out;
  padding: 20px; /* Add padding to allow some space around the image */
}

.modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.modal-content img {
  max-width: 70%;
  max-height: 100vh;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
  object-fit: contain;
}

.image-note {
  margin-top: 15px;
  font-size: 16px;
  color: #fff;
}
</style>
