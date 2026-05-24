# Newton-Raphson API — Solucionador de Sistemas de Ecuaciones No Lineales 2×2

## 1. Descripción General

El **Newton-Raphson API** es una aplicación REST construida con Spring Boot 3.4.x que implementa el método iterativo de Newton-Raphson para resolver sistemas de dos ecuaciones no lineales con dos incógnitas. El API acepta ecuaciones matemáticas como expresiones de texto (utilizando notación estándar), un vector inicial de aproximación, y parámetros de convergencia, retornando la solución con un historial detallado de cada paso iterativo.

Este proyecto fue desarrollado para fines académicos en la Universidad del Magdalena, Departamento de Matemáticas y Estadística, en el curso de Análisis Numérico. Constituye una herramienta educativa que permite a estudiantes e investigadores comprender en profundidad cómo funciona el método de Newton-Raphson, visualizando cada iteración, la evaluación del sistema, la matriz Jacobiana, el vector de corrección y el criterio de convergencia.

## 2. Fundamento Matemático

### El Problema: Sistemas de Ecuaciones No Lineales

Consideramos un sistema de dos ecuaciones no lineales en dos variables:

```
f₁(x, y) = 0
f₂(x, y) = 0
```

Este problema puede expresarse vectorialmente como **F(x) = 0**, donde **F**: ℝ² → ℝ² es una función vectorial y **x** = [x, y]ᵀ es el vector solución que buscamos. A diferencia de los sistemas lineales que pueden resolverse mediante álgebra lineal directa, los sistemas no lineales requieren métodos iterativos para aproximar la solución.

### El Método de Newton-Raphson

El método de Newton-Raphson generalizado a sistemas multivariables es un algoritmo iterativo que refina sucesivamente una aproximación inicial según la fórmula:

$$\mathbf{x}_{n+1} = \mathbf{x}_n - \mathbf{J}(\mathbf{x}_n)^{-1} \cdot \mathbf{F}(\mathbf{x}_n)$$

En la práctica, no invertimos la matriz Jacobiana explícitamente (lo cual es numericamente inestable). En su lugar, resolvemos el sistema lineal:

$$\mathbf{J}(\mathbf{x}_n) \cdot \Delta\mathbf{x} = -\mathbf{F}(\mathbf{x}_n)$$

Luego actualizamos: **x**_{n+1} = **x**_n + Δ**x**

Esta aproximación es más robusta numéricamente y es el enfoque utilizado en esta implementación.

### La Matriz Jacobiana

La matriz Jacobiana de la función **F** en el punto **x** es la matriz de derivadas parciales:

$$\mathbf{J}(\mathbf{x}) = \begin{bmatrix} 
\frac{\partial f_1}{\partial x} & \frac{\partial f_1}{\partial y} \\
\frac{\partial f_2}{\partial x} & \frac{\partial f_2}{\partial y}
\end{bmatrix}$$

Para un sistema 2×2, la Jacobiana es una matriz 2×2 que describe cómo cambian las funciones f₁ y f₂ con respecto a los cambios en x e y.

### Cálculo Numérico del Jacobiano

Dado que no siempre disponemos de las derivadas analíticas, calculamos las derivadas parciales numéricamente utilizando **diferencias finitas hacia adelante**:

$$\frac{\partial f_i}{\partial x_j} \approx \frac{f_i(\mathbf{x} + h \cdot \mathbf{e}_j) - f_i(\mathbf{x})}{h}$$

donde **e**_j es el j-ésimo vector unitario y h = 1×10⁻⁷ es un paso de diferenciación pequeño que equilibra la precisión numérica con la evitación de errores de redondeo.

### Criterio de Convergencia

El algoritmo itera hasta que se cumple uno de estos criterios:

1. **Convergencia alcanzada**: $\max(|\Delta x_0|, |\Delta x_1|) < \text{tolerance}$ (habitualmente tolerance = 1×10⁻⁷)
2. **Límite de iteraciones**: Se alcanzó el número máximo de iteraciones permitidas

El valor `error` reportado en cada paso iterativo es precisamente el máximo valor absoluto de los componentes de Δ**x**, que es el criterio usado para decidir convergencia.

### Solución del Sistema Lineal: Eliminación Gaussiana con Pivoteo Parcial

Para resolver **J** · Δ**x** = -**F** en cada iteración, utilizamos **eliminación gaussiana con pivoteo parcial**. Esta técnica:

1. **Construye una matriz aumentada** [**J**|**-F**]
2. **Realiza eliminación hacia adelante**, seleccionando en cada paso el pivote de mayor valor absoluto en la columna actual. Esto evita dividir por números muy pequeños, lo que causaría inestabilidad numérica.
3. **Verifica singularidad**: Si el pivote final es muy pequeño (< 1×10⁻¹²), la matriz es singular y se lanza una excepción.
4. **Realiza sustitución hacia atrás** para obtener Δ**x**

## 3. Arquitectura del Proyecto

### 3.1 Stack Tecnológico

| Componente | Versión | Propósito |
|-----------|---------|-----------|
| Java | 21 LTS | Lenguaje de programación base |
| Spring Boot | 3.4.x | Framework web y inyección de dependencias |
| Spring Validation | 3.4.x | Validación de entrada con anotaciones jakarta.validation |
| exp4j | 0.4.8 | Parser y evaluador de expresiones matemáticas |
| JUnit 5 | Incluido en starter-test | Framework para pruebas unitarias |
| Mockito | Incluido en starter-test | Mock objects para testing |
| Maven | 3.9+ | Gestor de dependencias y build |
| Azure App Service | - | Plataforma PaaS para despliegue |

### 3.2 Estructura de Paquetes

```
newton-raphson-api/
├── README.md                                 # Este archivo
├── pom.xml                                   # Configuración Maven
├── src/main/java/com/unimagdalena/newtonraphsonapi/
│   ├── NewtonRaphsonApiApplication.java      # Clase principal Spring Boot
│   ├── controller/
│   │   ├── SolverController.java             # Endpoint POST /api/v1/solve
│   │   └── HealthController.java             # Endpoint GET /api/v1/health (liveness)
│   ├── service/
│   │   └── NewtonRaphsonService.java         # Lógica del método Newton-Raphson
│   ├── util/
│   │   └── GaussianElimination.java          # Utilidad para resolver sistemas lineales
│   ├── model/
│   │   ├── request/
│   │   │   └── SolveRequest.java             # DTO de solicitud con validaciones
│   │   └── response/
│   │       ├── SolveResponse.java            # DTO de respuesta completa
│   │       └── IterationStep.java            # DTO para cada paso iterativo
│   └── exception/
│       ├── EquationParseException.java       # Excepción de parsing de expresiones
│       ├── SingularMatrixException.java      # Excepción de matriz singular
│       └── GlobalExceptionHandler.java       # Manejador centralizado de excepciones
└── src/test/java/com/unimagdalena/newtonraphsonapi/
    ├── util/GaussianEliminationTest.java     # Tests de eliminación gaussiana
    ├── service/NewtonRaphsonServiceTest.java # Tests del servicio
    └── controller/
        ├── SolverControllerTest.java         # Tests del controlador solver
        └── HealthControllerTest.java         # Tests del endpoint health
```

### 3.3 Flujo de una Solicitud HTTP

```
1. Cliente HTTP
   ↓
2. POST /api/v1/solve + JSON SolveRequest
   ↓
3. SolverController.solve() recibe la solicitud
   ↓
4. @Valid valida SolveRequest (es.unimagdalena.newtonraphsonapi.model.request.SolveRequest)
   - Verifica: equations (exactamente 2, no en blanco)
   - Verifica: variables (exactamente 2, no en blanco)
   - Verifica: initialGuess (exactamente 2 valores)
   - Verifica: maxIterations (1-500), tolerance (positivo)
   ↓
5. NewtonRaphsonService.solve(request) inicia el algoritmo
   ↓
6. BUCLE ITERATIVO (máximo maxIterations veces):
   a. evaluateSystem() → Evalúa F(x) con expresiones exp4j
   b. computeJacobian() → Calcula matriz Jacobiana con diferencias finitas
   c. GaussianElimination.solve(J, -F) → Resuelve J·Δx = -F
   d. Actualiza x = x + Δx
   e. Calcula error = max(|Δx[0]|, |Δx[1]|)
   f. Construye IterationStep para el historial
   g. Si error < tolerance → converged=true, sale del bucle
   ↓
7. Construye SolveResponse con resultados finales
   ↓
8. GlobalExceptionHandler captura excepciones (si las hay)
   - EquationParseException → HTTP 422
   - SingularMatrixException → HTTP 422
   - MethodArgumentNotValidException → HTTP 400
   - Exception genérica → HTTP 500
   ↓
9. ResponseEntity<SolveResponse> se serializa a JSON
   ↓
10. Cliente recibe HTTP 200 + JSON (o error HTTP Code + ErrorResponse)
```

## 4. API Reference

### POST /api/v1/solve

**Descripción**: Resuelve un sistema 2×2 de ecuaciones no lineales mediante Newton-Raphson.

**URL**: `POST http://localhost:8080/api/v1/solve`

#### Parámetros de Solicitud

| Campo | Tipo | Requerido | Default | Descripción |
|-------|------|-----------|---------|-------------|
| `equations` | Array[String] | Sí | — | Exactamente 2 expresiones matemáticas. Cada una representa f_i(x,y) = 0 |
| `variables` | Array[String] | Sí | — | Exactamente 2 nombres de variables (ej: ["x", "y"]) |
| `initialGuess` | Array[Double] | Sí | — | Aproximación inicial [x₀, y₀]. Crítica para convergencia |
| `maxIterations` | Integer | No | 100 | Máximo número de iteraciones. Rango: 1-500 |
| `tolerance` | Double | No | 1e-7 | Criterio de convergencia. Debe ser positivo. Ej: 1e-10 |

#### Ejemplo de Solicitud

```json
{
  "equations": [
    "x^2 + y - 4",
    "x + y^2 - 6"
  ],
  "variables": [
    "x",
    "y"
  ],
  "initialGuess": [1.0, 1.0],
  "maxIterations": 100,
  "tolerance": 1e-7
}
```

#### Parámetros de Respuesta (SolveResponse)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `converged` | Boolean | true si se alcanzó la tolerancia antes de maxIterations |
| `variables` | Array[String] | Echo de los nombres de variables de la solicitud |
| `solution` | Map<String, Double> | Solución final: nombre_variable → valor (redondeado a 10 decimales) |
| `totalIterations` | Integer | Número total de iteraciones realizadas |
| `finalError` | Double | Valor del error en la última iteración |
| `steps` | Array[IterationStep] | Historial completo de iteraciones |

**IterationStep** (estructura anidada en `steps`):

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `iterationNumber` | Integer | Número de iteración (comenzando en 1) |
| `xValues` | Array[Double] | Aproximación actual [x_n, y_n] |
| `fValues` | Array[Double] | Evaluación del sistema [f₁(x_n, y_n), f₂(x_n, y_n)] |
| `jacobian` | Array[Array[Double]] | Matriz Jacobiana 2×2 en el punto actual |
| `deltaX` | Array[Double] | Corrección calculada [Δx, Δy] |
| `nextXValues` | Array[Double] | Siguiente aproximación [x_n + Δx, y_n + Δy] |
| `error` | Double | Criterio de convergencia: max(\|Δx\|, \|Δy\|) |

#### Ejemplo de Respuesta (Éxito - 2 iteraciones)

```json
{
  "converged": true,
  "variables": ["x", "y"],
  "solution": {
    "x": 1.5513670480,
    "y": 2.4486329520
  },
  "totalIterations": 5,
  "finalError": 8.234567e-8,
  "steps": [
    {
      "iterationNumber": 1,
      "xValues": [1.0, 1.0],
      "fValues": [0.0, -4.0],
      "jacobian": [[2.0, 1.0], [1.0, 2.0]],
      "deltaX": [0.4, 1.2],
      "nextXValues": [1.4, 2.2],
      "error": 1.2
    },
    {
      "iterationNumber": 2,
      "xValues": [1.4, 2.2],
      "fValues": [-0.04, -0.24],
      "jacobian": [[2.8, 1.0], [1.0, 4.4]],
      "deltaX": [0.1513, 0.2486],
      "nextXValues": [1.5513, 2.4486],
      "error": 0.2486
    }
  ]
}
```

#### Respuestas de Error

**400 Bad Request — VALIDATION_ERROR**
```json
{
  "error": "VALIDATION_ERROR",
  "message": "equations: Exactamente 2 ecuaciones son requeridas | initialGuess: El vector inicial no puede ser nulo",
  "status": 400
}
```

**422 Unprocessable Entity — EQUATION_PARSE_ERROR**
```json
{
  "error": "EQUATION_PARSE_ERROR",
  "message": "Failed to evaluate expression: 'x ** y + garbage$$' — Unknown function: **",
  "status": 422
}
```

**422 Unprocessable Entity — SINGULAR_MATRIX**
```json
{
  "error": "SINGULAR_MATRIX",
  "message": "Jacobian matrix is singular at this iteration. The system may have no solution or Newton-Raphson diverged.",
  "status": 422
}
```

**500 Internal Server Error — INTERNAL_ERROR**
```json
{
  "error": "INTERNAL_ERROR",
  "message": "An unexpected error occurred. Please check your input.",
  "status": 500
}
```

### GET /api/v1/health

**Descripción**: Verifica que el servicio está activo y disponible.

**URL**: `GET http://localhost:8080/api/v1/health`

**Respuesta (HTTP 200)**:
```json
{
  "status": "UP",
  "service": "newton-raphson-api"
}
```

## 5. Cómo Ejecutar el Proyecto Localmente

### Prerrequisitos

- **Java 21+** (JDK): Descarga desde [oracle.com](https://www.oracle.com/java/technologies/downloads/) o usa `sdkman`
  ```bash
  sdk install java 21.0.1-oracle
  sdk use java 21.0.1-oracle
  ```
- **Maven 3.9+**: Descarga desde [maven.apache.org](https://maven.apache.org/download.cgi) o usa `brew install maven` (macOS)

### Pasos de Instalación y Ejecución

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/newton-raphson-api.git
   cd newton-raphson-api
   ```

2. **Compila e instala dependencias**
   ```bash
   mvn clean install
   ```
   Esto descargará todas las dependencias y compilará el proyecto.

3. **Ejecuta la aplicación**
   ```bash
   mvn spring-boot:run
   ```
   O si prefieres ejecutar el JAR compilado:
   ```bash
   mvn clean package
   java -jar target/newton-raphson-api-0.0.1-SNAPSHOT.jar
   ```

4. **Verifica que el servidor está activo**
   ```bash
   curl http://localhost:8080/api/v1/health
   ```
   Deberías recibir:
   ```json
   {"status":"UP","service":"newton-raphson-api"}
   ```

5. **Prueba el endpoint principal**
   ```bash
   curl -X POST http://localhost:8080/api/v1/solve \
     -H "Content-Type: application/json" \
     -d '{
       "equations": ["x + y - 3", "x - y - 1"],
       "variables": ["x", "y"],
       "initialGuess": [0.0, 0.0]
     }'
   ```
   Respuesta esperada:
   ```json
   {"converged":true,"variables":["x","y"],"solution":{"x":2.0,"y":1.0},...}
   ```

6. **Ejecuta los tests**
   ```bash
   mvn test
   ```
   Deberías ver que todos los tests pasan (13 tests en total).

## 6. Despliegue en Azure App Service

### Pasos para Despliegue en Azure

1. **Empaqueta la aplicación sin ejecutar tests**
   ```bash
   mvn clean package -DskipTests
   ```
   Esto crea `target/newton-raphson-api-0.0.1-SNAPSHOT.jar`

2. **Inicia sesión en Azure**
   ```bash
   az login
   ```
   Se abrirá el navegador para autenticación.

3. **Crea un grupo de recursos** (si aún no existe)
   ```bash
   az group create \
     --name newton-raphson-rg \
     --location eastus
   ```

4. **Crea un App Service Plan**
   ```bash
   az appservice plan create \
     --name newton-raphson-plan \
     --resource-group newton-raphson-rg \
     --sku B1 \
     --is-linux
   ```

5. **Crea la Web App con Java 21 runtime**
   ```bash
   az webapp create \
     --resource-group newton-raphson-rg \
     --plan newton-raphson-plan \
     --name newton-raphson-api \
     --runtime "java|21"
   ```

6. **Configura la variable de entorno PORT** (Azure proporciona dinámica)
   ```bash
   az webapp config appsettings set \
     --resource-group newton-raphson-rg \
     --name newton-raphson-api \
     --settings PORT=8080
   ```

7. **Despliega el JAR**
   ```bash
   az webapp deploy \
     --resource-group newton-raphson-rg \
     --name newton-raphson-api \
     --src-path target/newton-raphson-api-0.0.1-SNAPSHOT.jar \
     --type jar
   ```

8. **Verifica el despliegue**
   ```bash
   curl https://newton-raphson-api.azurewebsites.net/api/v1/health
   ```

### Notas sobre Azure App Service

- Azure proporciona dinámicamente un puerto a través de la variable de entorno `PORT`. El fichero `application.properties` está configurado para usar `${PORT:8080}` que respeta esta variable.
- El despliegue puede tomar 2-3 minutos. Verifica los logs en Azure Portal.
- Para ver logs en tiempo real: `az webapp log tail --resource-group newton-raphson-rg --name newton-raphson-api`

## 7. Manejo de Errores

### Códigos de Error API

| HTTP Status | Código de Error | Causa | Solución Sugerida |
|-------------|-----------------|-------|-------------------|
| 400 | `VALIDATION_ERROR` | Un parámetro de solicitud no cumple validaciones (tamaño de listas, tipos de datos, etc.) | Revisa el mensaje detallado. Asegúrate de enviar exactamente 2 ecuaciones, 2 variables y 2 valores iniciales |
| 422 | `EQUATION_PARSE_ERROR` | Una expresión matemática no puede ser parseada por exp4j (sintaxis inválida, variables no definidas, etc.) | Revisa la sintaxis de tus expresiones. Usa operadores válidos: `+`, `-`, `*`, `/`, `^`, `sin`, `cos`, etc. |
| 422 | `SINGULAR_MATRIX` | La matriz Jacobiana es singular o near-singular en la iteración actual | Cambien el vector inicial o revisén que el sistema tiene solución única en la región de aproximación |
| 500 | `INTERNAL_ERROR` | Error inesperado en el servidor | Contacta al administrador o verifica los logs del servidor |

### Causas Comunes de Fallos

1. **VALIDATION_ERROR**: Usualmente por cantidad incorrecta de ecuaciones, variables o valores iniciales
2. **EQUATION_PARSE_ERROR**: Sintaxis inválida. Ejemplo: `x ** y` (** no es válido; usar `x ^ y`), `garbage$$`, variables no declaradas en el array
3. **SINGULAR_MATRIX**: El sistema es singular en la aproximación inicial o Newton-Raphson está divergiendo. Intenta un vector inicial diferente.
4. **No converge**: Si recibes `converged: false`, aumenta `maxIterations` o reduce `tolerance`. Algunos sistemas requieren más iteraciones.

## 8. Decisiones de Diseño

### Por qué exp4j en lugar de ecuaciones hardcodeadas

Utilizamos la biblioteca **exp4j** para permitir que los usuarios envíen ecuaciones como strings. Esto maximiza la flexibilidad: cualquier expresión matemática válida puede ser resuelta sin modificar el código. La alternativa (hardcodear cada sistema específico) sería inflexible e impráctica para una herramienta científica. exp4j es ligero, rápido y ampliamente usado en aplicaciones numéricas.

### Por qué Jacobiano numérico en lugar de simbólico

Calcular el Jacobiano numéricamente mediante diferencias finitas (h = 1e-7) simplifica enormemente el desarrollo. La alternativa sería derivación simbólica, lo que requeriría analizar y diferenciar automáticamente las expresiones (muy complejo). El Jacobiano numérico es suficientemente preciso para la convergencia y más robusto ante expresiones complicadas. Su costo computacional es mínimo (4 evaluaciones de función por iteración).

### Por qué Eliminación Gaussiana con Pivoteo Parcial

Resolvemos el sistema lineal J·Δx = -F en cada iteración usando **eliminación gaussiana con pivoteo parcial** en lugar de invertir J directamente. Razones:

1. **Estabilidad numérica**: El pivoteo evita dividir por números muy pequeños (que causaría pérdida de precisión)
2. **Eficiencia**: O(n³) pero para n=2 es trivial. No requiere cálculo de determinante
3. **Detección de singularidad**: Naturalmente detecta si la matriz es singular
4. Alternativas como **Regla de Cramer** sería más lenta y teóricamente igual de inestable

### Por qué estructura step-by-step en la respuesta

Retornamos **cada paso iterativo completo** (xValues, fValues, Jacobiano, deltaX, error) porque tiene valor educativo inmediato. Estudiantes e investigadores pueden ver exactamente qué está sucediendo en cada iteración: cómo se comporta la función, cómo se actualiza la aproximación, cuándo se alcanza convergencia. Esto es inestimable para propósitos académicos y debugging. Una alternativa sería retornar solo el resultado final, pero sería menos instructiva.

### Por qué sin base de datos ni autenticación

Este proyecto es un **MVP (Minimum Viable Product)** con alcance académico e investigativo. No incluimos:
- **Base de datos**: El servicio es **stateless** (sin estado). Cada solicitud es independiente. Si se necesitara persistencia, se agregaría fácilmente (PostgreSQL, MongoDB).
- **Autenticación/Autorización**: Asumimos un entorno de uso educativo. En producción real, se agregaría OAuth2, API keys, etc.

El enfoque stateless permite escalabilidad horizontal en cloud (múltiples instancias detrás de un load balancer).

## 9. Limitaciones y Trabajo Futuro

### Limitaciones Actuales

- **Solo sistemas 2×2**: El código está específicamente escrito para problemas con exactamente 2 ecuaciones y 2 incógnitas. Sistemas NxN requerirían refactorización mayor.
- **Jacobiano numérico solamente**: No hay soporte para proporcionar Jacobiano analítico, lo cual podría mejorar precisión en algunos casos.
- **Convergencia depende fuertemente de x₀**: Newton-Raphson es un método local. Una mala aproximación inicial puede no converger o converger a raíces no deseadas. No hay garantía de encontrar todas las soluciones.
- **Sin soporte para raíces complejas**: El algoritmo trabaja solo con números reales.
- **Sin visualización gráfica**: Retornamos datos, pero no hay interfaz web para graficar el comportamiento de las funciones o el proceso iterativo.

### Mejoras Futuro

- **Generalización a NxN**: Extender el servicio a sistemas de cualquier tamaño, manteniendo la respuesta step-by-step.
- **Derivación simbólica**: Usar librerías como Sympy (Python) via subprocess para Jacobiano exacto (si los usuarios lo solicitan).
- **Métodos alternativos**: Agregar endpoints para resolver con método de bisección, secante, etc., permitiendo comparación empírica.
- **API de convergencia gráfica**: Endpoint que retorne datos para graficar ||F(x)|| vs iteración.
- **Web UI**: Interfaz gráfica React/Vue donde usuarios ingrese ecuaciones e vean resultados interactivos.
- **Autenticación OAuth2**: Proteger endpoint, permitir historial por usuario.
- **Base de datos PostgreSQL**: Persistir solicitudes y resultados para análisis.
- **Docker/Kubernetes**: Containerizar para despliegue más sencillo en sistemas orchestrados.

## 10. Glosario

### Sistema de Ecuaciones No Lineales
Conjunto de dos o más ecuaciones en la forma f₁(x₁, x₂, ...) = 0, f₂(x₁, x₂, ...) = 0, etc., donde al menos una función es no lineal (contiene términos como x², sin(x), eˣ, etc.). A diferencia de sistemas lineales (Ax = b), estos no tienen solución por álgebra lineal directa y requieren iteración numérica.

### Método de Newton-Raphson
Algoritmo iterativo que encuentra raíces de f(x) = 0 (o en n dimensiones, **F**(**x**) = 0) comenzando desde una aproximación inicial y refinándola sucesivamente. Cada iteración requiere la evaluación de la función y su derivada (o Jacobiano en multivariables). Converge cuadráticamente cerca de la raíz si la derivada no es cero.

### Matriz Jacobiana
Matriz de derivadas parciales de una función vectorial **F**: ℝⁿ → ℝⁿ. Cada fila contiene las derivadas parciales de una función f_i con respecto a todas las variables. La Jacobiana es el análogo del "derivada" en cálculo multivariable: describe cómo cambia **F** en respuesta a cambios infinitesimales en **x**.

### Eliminación Gaussiana con Pivoteo Parcial
Técnica numérica para resolver sistemas lineales Ax = b. Procede eliminando variables sucesivamente mediante manipulación de filas, pero antes de cada eliminación, busca el **pivote** (elemento de mayor valor absoluto en la columna actual) y lo coloca en la diagonal. Esto evita numéricamente dividir por números pequeños, mejorando estabilidad. Alternativa al cálculo directo de A⁻¹.

### Diferencias Finitas hacia Adelante
Método para aproximar derivadas numéricamente usando valores de la función en puntos cercanos. Para una derivada de f en x: f'(x) ≈ (f(x+h) - f(x))/h. Es "hacia adelante" porque usa un punto adelante (x+h) en lugar de atrás o centrado. Introduce error, pero es útil cuando derivadas analíticas no están disponibles. En esta implementación, h = 1×10⁻⁷.

### Convergencia
Propiedad de un método iterativo de aproximarse cada vez más a la solución verdadera conforme avanzan las iteraciones. Para Newton-Raphson con convergencia cuadrática, el error se reduce al cuadrado cada iteración, significando que el número de dígitos correctos se duplica aproximadamente cada paso (finalmente). Se verifica midiendo max(|Δx_i|) < tolerancia.

### Tolerancia
Umbral de error bajo el cual consideramos que una solución numérica es lo suficientemente precisa. En nuestro API, la tolerancia por defecto es 1×10⁻⁷. Si el error en una iteración (max(|Δx|)) cae por debajo de la tolerancia, el algoritmo se detiene y reporta convergencia. Tolerancias más pequeñas requieren más iteraciones.

## 11. Referencias

Burden, R. L., & Faires, J. D. (2015). *Numerical Analysis* (10th ed.). Cengage Learning.

Chapra, S. C., & Canale, R. P. (2015). *Numerical Methods for Engineers* (7th ed.). McGraw-Hill Education.

Golub, G. H., & Van Loan, C. F. (2013). *Matrix Computations* (4th ed.). Johns Hopkins University Press.

ObjectHunter. (n.d.). *exp4j - Mathematical Expression Evaluator*. Recuperado de https://www.objecthunter.net/exp4j/

Spring Foundation. (2024). *Spring Boot Documentation*. Recuperado de https://spring.io/projects/spring-boot

Strang, G. (2019). *Linear Algebra and Learning from Data*. Wellesley-Cambridge Press.

Microsoft. (2024). *Azure App Service - Java quickstart*. Recuperado de https://learn.microsoft.com/en-us/azure/app-service/quickstart-java

---

**Autor**: Equipo de Desarrollo - Universidad del Magdalena, Departamento de Matemáticas y Estadística  
**Curso**: Análisis Numérico  
**Última actualización**: Mayo 2026  
**Licencia**: MIT  
**Contacto académico**: (Correo del profesor/departamento)

