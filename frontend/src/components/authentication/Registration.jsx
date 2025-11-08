import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

import {Typography, Grid, TextField, Container, Box, Button} from '@mui/material'
import { register } from "../../services/authapi";

function Registration(){

    const [formData, setFormData] = useState({
        username: '',
        password: '',
        confirmPassword: ''
    })

    const [error, setError] = useState('')
    const navigate = useNavigate()

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    }


    const handleSubmit = async (e) => {
        e.preventDefault();

        if(formData.password != formData.confirmPassword){
            setError('Passwords do not match')
            return
        }
        setError('')

        try{
            const registrationResponse = await register(formData);

            if(registrationResponse.status == 201){
                navigate('/registrationSuccessful')
            }else{
                const errorText = registrationResponse.data.response;
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
                Register
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
                    <Grid item xs = {12}>
                    <TextField
                            label="Confirm Password"
                            name="confirmPassword"
                            type = "password"
                            fullWidth
                            value = {formData.confirmPassword}
                            onChange = {handleChange}
                            required
                        />
                    </Grid>
                    <Typography variant="h6" align="center" gutterBottom>
                       Already registered? Login  <a href="/login">here</a>
                    </Typography>
                    {error && (
                        <Grid item xs = {12}>
                            <Typography color="error" variant="body2">
                                {error}
                            </Typography>
                        </Grid>
                    )}
                    <Grid item xs = {12}>
                        <Button type="submit" fullWidth variant="contained" color="primary">
                            Register
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Box>
    </Container>
    )
}


export default Registration