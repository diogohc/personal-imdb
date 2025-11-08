import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

import {Typography, Grid, TextField, Container, Box, Button} from '@mui/material'
import { loginUser } from "../../services/authapi";

import { useAuth } from "./AuthContext";

function Login(){

    const [formData, setFormData] = useState({
        username: '',
        password: '',
    })

    const [error, setError] = useState('')
    const navigate = useNavigate()

    const setToken = useAuth()

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    }


    const handleSubmit = async (e) => {
        e.preventDefault();

        try{
            const loginResponse = await loginUser(formData);

            if(loginResponse.status == 200){
                localStorage.setItem('accessToken', loginResponse.data.token)
                navigate('/home')
            }else{
                const errorText = loginResponse.data.response;
                setError(errorText)
            }
        } catch(error){
            setError("An error ocurred:", error.message)
        }
    }



    return(
        <Container maxWidth = "xs">
        <Box sx={{mt:8}}>
            <Typography variant="h5" align="center" gutterBottom>
                Login
            </Typography>

            <form onSubmit={handleSubmit}>
                <Grid container spacing = {2}>
                    <Grid item xs = {12}>
                        <TextField
                            label="Username"
                            name="username"
                            fullWidth
                            value = {FormData.username}
                            onChange = {handleChange}
                            required
                        />
                    </Grid>
                    <Grid item xs = {12}>
                    <TextField
                            label="Password"
                            name="password"
                            type = "password"
                            fullWidth
                            value = {formData.password}
                            onChange = {handleChange}
                            required
                        />
                    </Grid>

                    {error && (
                        <Grid item xs = {12}>
                            <Typography color="error" variant="body2">
                                {error}
                            </Typography>
                        </Grid>
                    )}
                    <Grid item xs = {12}>
                        <Button type="submit" fullWidth variant="contained" color="primary">
                            Login
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Box>
    </Container>
    )
}


export default Login