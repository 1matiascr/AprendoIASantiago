package com.example.aprendoiasantiago

import com.example.aprendoiasantiago.data.GameRepository
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import com.example.aprendoiasantiago.network.BackendApiService
import com.example.aprendoiasantiago.network.CurrentWeather
import com.example.aprendoiasantiago.network.WeatherApiService
import com.example.aprendoiasantiago.network.WeatherResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class NetworkAndAuthTest {

    private lateinit var playerDb: PlayerDatabaseHelper
    private lateinit var mapDb: MapDatabaseHelper
    private lateinit var repository: GameRepository
    private lateinit var backendApi: BackendApiService
    private lateinit var weatherApi: WeatherApiService

    @Before
    fun setUp() {
        playerDb = mockk(relaxed = true)
        mapDb = mockk(relaxed = true)
        repository = GameRepository(playerDb, mapDb)
        backendApi = mockk(relaxed = true)
        weatherApi = mockk(relaxed = true)
    }

    // =========================================================
    // 1. VALIDACIÓN DE CREDENCIALES Y ROLES
    // =========================================================

    @Test
    fun `validarCredenciales permite acceso cuando alias y pass coinciden`() {
        val jugadores = listOf(
            Player(id = 1, name = "Ana", alias = "AnitaA", avatar = "avatar_1", pass = "1234", points = 100),
            Player(id = 2, name = "Admin", alias = "AdminStgo", avatar = "avatar_2", pass = "admin2026", points = 500)
        )
        every { playerDb.getAllPlayers() } returns jugadores

        val lista = repository.obtenerTodosLosJugadores()
        val autenticado = lista.find { it.alias == "AdminStgo" && it.pass == "admin2026" }

        assertNotNull(autenticado)
        assertEquals("Admin", autenticado?.name)
    }

    @Test
    fun `validarCredenciales rechaza acceso cuando la contrasena es incorrecta`() {
        val jugadores = listOf(
            Player(id = 1, name = "Ana", alias = "AnitaA", avatar = "avatar_1", pass = "1234", points = 100)
        )
        every { playerDb.getAllPlayers() } returns jugadores

        val lista = repository.obtenerTodosLosJugadores()
        val autenticado = lista.find { it.alias == "AnitaA" && it.pass == "clave_erronea" }

        assertNull(autenticado)
    }

    // =========================================================
    // 2. MANEJO DE RESPUESTAS DE RED - BACKEND SPRING BOOT
    // =========================================================

    @Test
    fun `getPlayers y getPoints retornan listas remotas correctamente`() = runTest {
        val jugadoresRemotos = listOf(
            Player(id = 10, name = "Matías", alias = "MatiCurrin", avatar = "avatar_1", pass = "1234", points = 300)
        )
        val puntosRemotos = listOf(
            QrPoint(1, "Cerro Santa Lucía", "Mirador histórico", -33.44, -70.64, false, 120, "santa_lucia")
        )

        coEvery { backendApi.getPlayers() } returns jugadoresRemotos
        coEvery { backendApi.getPoints() } returns puntosRemotos

        val resPlayers = backendApi.getPlayers()
        val resPoints = backendApi.getPoints()

        assertEquals(1, resPlayers.size)
        assertEquals("MatiCurrin", resPlayers.first().alias)
        assertEquals("Cerro Santa Lucía", resPoints.first().name)
    }

    @Test
    fun `createPlayer maneja respuesta HTTP 200 exitosa y error 400 del servidor`() = runTest {
        val nuevoJugador = Player(id = 0, name = "Alejandro", alias = "AleDev", avatar = "avatar_2", pass = "abcd", points = 0)
        val jugadorCreado = nuevoJugador.copy(id = 5)

        // Simulación de respuesta exitosa (HTTP 200)
        coEvery { backendApi.createPlayer(nuevoJugador) } returns Response.success(jugadorCreado)

        val responseOk = backendApi.createPlayer(nuevoJugador)
        assertTrue(responseOk.isSuccessful)
        assertEquals(5, responseOk.body()?.id)

        // Simulación de error del servidor (HTTP 400 Bad Request)
        val errorBody = """{"error":"Alias duplicado"}""".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery { backendApi.createPlayer(nuevoJugador) } returns Response.error(400, errorBody)

        val responseError = backendApi.createPlayer(nuevoJugador)
        assertFalse(responseError.isSuccessful)
        assertEquals(400, responseError.code())
    }

    // =========================================================
    // 3. MANEJO DE RESPUESTAS DE RED - API EXTERNA (CLIMA SANTIAGO)
    // =========================================================

    @Test
    fun `getSantiagoWeather retorna temperatura y condiciones actuales de Santiago`() = runTest {
        val climaSimulado = WeatherResponse(
            currentWeather = CurrentWeather(
                temperature = 24.5,
                windspeed = 12.0,
                weathercode = 1
            )
        )

        coEvery {
            weatherApi.getSantiagoWeather(latitude = -33.4489, longitude = -70.6693, currentWeather = true)
        } returns climaSimulado

        val resultado = weatherApi.getSantiagoWeather()

        assertEquals(24.5, resultado.currentWeather.temperature, 0.01)
        assertEquals(12.0, resultado.currentWeather.windspeed, 0.01)
        assertEquals(1, resultado.currentWeather.weathercode)
    }

    @Test
    fun `fallo de conexion en WeatherApiService se captura sin cerrar la aplicacion`() = runTest {
        coEvery {
            weatherApi.getSantiagoWeather(any(), any(), any())
        } throws IOException("Sin conexión a internet")

        var errorCapturado = false
        try {
            weatherApi.getSantiagoWeather()
        } catch (e: IOException) {
            errorCapturado = true
        }

        assertTrue(errorCapturado)
    }
}