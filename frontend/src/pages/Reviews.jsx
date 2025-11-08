import "../css/Reviews.css"
import { useState, useEffect } from "react"
import MovieCard from "../components/MovieCard"
import { getReviews } from '../services/reviewapi'

function Reviews(){
    const [reviews, setReviews] = useState([])


    useEffect(() => {
            const loadReviews = async () => {
                try{
                    const reviews = await getReviews();
                    setReviews(reviews)
                } catch (err){
                    console.log(err)
                    setError("failed to load movies")
                } 
            }
    
            loadReviews()
        }, [])

    return (
        <>
    {reviews.length==0 ?             
            <div className="reviews-empty">
                <h2>No reviews yet</h2>
            </div>
            
            :        
            <div className="home"> 
                <div className="movies-grid">
                    {reviews.map(review => (
                        <MovieCard movie={review} key={review.movieId}/>
                        ))}
                </div>
            </div>
            }
        </>
    )
    
}

export default Reviews