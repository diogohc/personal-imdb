import '../css/MovieCard.css'

function MovieCard({movie}){
    function onFavoriteClick(){
        alert("click")
    }

    return  (
    <div className="movie-card">
            <div className="movie-poster">
                <img src={movie.moviePoster} alt={movie.title}/>
            </div>
            <div className="movie-info">
                <h3>{movie.movieTitle}</h3>
                <p>{movie.releaseYear} {movie.reviewRating > 0 ? " | "+movie.reviewRating : null}</p>
            </div>
        </div>
    )
}

export default MovieCard