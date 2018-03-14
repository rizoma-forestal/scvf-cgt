# Gestión de trazabilidad de los productos

Descripción de la aplicación:
-----------------------------

Esta aplicación es parte del subsistema de orden nacional del SACVeFor, tiene por objeto gestionar la trazabilidad de los productos forestales a partir de la recepción de la Guía que ampara el primer movimiento del producto desde el predio de extracción. Todos los movimientos de productos, con o sin agregado de valor se realizarán mediante esta aplicación que se comunica con los subsistemas locales y el componente de Control y Verificación mediante su API REST. Asimismo, para los datos comunes, consume las API correspondientes del ámbito nacional.

La primera versión tendrá como alcance la acreditación de las Guías primarias y acreditación en la cuenta del usuario.

Descripción de la API de servicios:
-----------------------------------

La aplicación cuenta con una API de servicios REST que permiten tanto consulta y selección de las entidades en cuestión como su registro y edición. Los usuarios con acceso a estos servicios serán los subistemas locales y el componente de Control y Verificación.
