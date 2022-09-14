import axios from 'axios'

const backendURI = process.env.REACT_APP_BACKEND_URI

const instance = axios.create({
    baseURL: backendURI,
    headers: {
        'Access-Control-Allow-Origin' : '*',
        'Access-Control-Allow-Methods':'GET,PUT,POST,DELETE,PATCH,OPTIONS',
    }
})

export default instance