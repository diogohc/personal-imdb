import '../css/Home.css'
import { useState, useEffect } from "react"
import MovieCard from "../components/MovieCard"
import { getMovies } from '../services/movieapi'

function Home(){
    const [searchQuery, setSearchQuery] = useState("")
    const [movies, setMovies] = useState([])
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const loadMovies = async () => {
            try{
                const movies = await getMovies();
                setMovies(movies)
            } catch (err){
                console.log(err)
                setError("failed to load movies")
            } finally{
                setLoading(false)
            }
        }

        loadMovies()
    }, [])   

    const handleSearch = (e) => {
        e.preventDefault()
    }

    return (
        <div className="home">
            <form onSubmit={handleSearch} className="search-form">
                <input type="text" 
                placeholder="Search for movies..." 
                className="search-input" 
                value={searchQuery}
                onChange={(e)=> setSearchQuery(e.target.value)}
                />
                <button type="submit" className="search-button">Search</button>
            </form>

            {error  && <div className='error-message'>{error} </div>}

            <div className="movies-grid">
                {movies.map(movie => (
                    <MovieCard movie={movie} key={movie.movieId}/>
                    ))}
            </div>
        </div>
    )
}

export default Home