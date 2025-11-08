import React, { createContext, useContext, useState } from "react";


const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext)

export const AuthProvider = (children) => {
    const [accessToken, setAccessToken] = useState(() => {
        return localStorage.getItem('accessToken')
    })


    const setToken = (token) => {
        setAccessToken(token)
        localStorage.setItem('accessToken', token)
    }

    const logout = () => {
        setAccessToken(null)
        localStorage.removeItem('acessToken')
    }

    return (
        <AuthContext.Provider value = {{accessToken, setToken, logout}}>
            {children}
        </AuthContext.Provider>
    )
}