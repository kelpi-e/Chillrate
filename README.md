![изображение](https://github.com/user-attachments/assets/a982c195-5cbd-4be7-87d6-62c4e8049a11)

Figma link: https://www.figma.com/design/Uwdvf0r4W9qgSXTUNoxJFM/Untitled?node-id=9-60&t=SQACnfOmLxUuUbu1-1

95.174.104.223 - host
8099 - port 
/api/v1/auth/regAdmin -регистрация тренера (method:post,body:{email,password,name})
/api/v1/auth/register -регистрация пользователя (method:post,body:{email,password,name})
/api/v1/auth/authenticate -авторизация (method:post,body:{email,password})
/api/v1/test -тестовый endpoint,закрыт для незарегистрированных пользователей(method:get,headers:{Authorization:Client <jwt>})
