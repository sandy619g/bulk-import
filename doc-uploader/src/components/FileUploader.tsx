import React, { useState } from 'react'
import axios from 'axios'

function FileUploader() {
  const [file, setFile] = useState(null)
  const [message, setMessage] = useState('')
  const [uploading, setUploading] = useState(false)

  const backendUrl = import.meta.env.VITE_API_BASE_URL

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0]
    if (selectedFile && selectedFile.type === 'text/csv') {
      setFile(selectedFile)
      setMessage('')
    } else {
      setFile(null)
      setMessage('Please select a valid CSV file.')
    }
  }

  const handleUpload = async () => {
    if (!file) {
      setMessage('No file selected.')
      return
    }

    const formData = new FormData()
    formData.append('file', file)

    try {
      setUploading(true)
      setMessage('Uploading file...')

      const res = await axios.post(`${backendUrl}/api/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })

      const fileId = res.data
      setMessage('File uploaded. Processing...')
      pollStatus(fileId)
    } catch (err) {
      console.error(err)
      setMessage('Upload failed.')
    } finally {
      setUploading(false)
    }
  }

  const pollStatus = (fileId) => {
    const interval = setInterval(async () => {
      try {
        const res = await axios.get(`${backendUrl}/api/status?id=${fileId}`)
        const status = res.data

        if (status === 'COMPLETED') {
          setMessage('File processed successfully.')
          clearInterval(interval)
        } else if (status === 'FAILED') {
          setMessage('File processing failed.')
          clearInterval(interval)
        } else {
          setMessage(`Processing... (${status})`)
        }
      } catch (err) {
        console.error(err)
        setMessage('Error checking status.')
        clearInterval(interval)
      }
    }, 2000)
  }

  return (
    <div className="container">
      <h1>Bulk User Importer</h1>
      <input type="file" accept=".csv" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={uploading}>
        {uploading ? 'Uploading...' : 'Upload'}
      </button>
      <p className="message">{message}</p>
    </div>
  )
}

export default FileUploader
