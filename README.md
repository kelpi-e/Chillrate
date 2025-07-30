![изображение](https://github.com/user-attachments/assets/a982c195-5cbd-4be7-87d6-62c4e8049a11)

Figma link: https://www.figma.com/design/Uwdvf0r4W9qgSXTUNoxJFM/Untitled?node-id=9-60&t=SQACnfOmLxUuUbu1-1
<p>
95.174.104.223 - host(в общаге летом не работает)
<p>
192.168.0.96:8099 -host(работает когда я в ростове)
<p>
8099 - port  
<p>
headers:{Authorization:'Client ~jwt~'} авторизация на сервере
<p>
public endpoints:
<p>
api/v1/auth/regAdmin -регистрация тренера (method:post,body:{email,password,name})
<p>
api/v1/auth/register -регистрация пользователя (method:post,body:{email,password,name})
<p>
api/v1/auth/authenticate -авторизация (method:post,body:{email,password})
<p>
private endpoints(any roles)
<p>
api/v1/test -тестовый endpoint,закрыт для незарегистрированных пользователей(method:get)
<p>
api/v1/client/{uuid} ссылка на распрееленгие по командам(method:get)
<p>
private endpoints(Admin role)
<p>
api/v1/admin/team/{teamId}/{userId} принятие пользователя в команду(method:get)
<p>
api/v1/admin/team/{teamId}/{userId} удаление пользовател ищ команды(method:delete)
<p>
api/v1/admin/team добавление команды (method:post body:{name})
<p>
api/v1/admin/team измененение имени команды (method:put,body:{name})
<p>
api/v1/admin/team/{id} удаление команды(method:delete)
<p>
api/v1/admin/team/{id} поучить данные о команде(method:get)


UDP ENDPOINT:
1) port:8080
Отправка данных с датчика на сервер
принимет принимает json с параметрами jwt(String), dateTime(pattern: yyyy-MM-dd HH:mm:ss), data(String-json)
Возращает строку "OK"-удалось сохранить данные; "jwt Invalid"-ошибка из-за jwt;  "Parse exception"-ошибка в переанныз  данных
2) port:8081
Взятие данных с сервера на админку
отправка сообщений в виде json с параметрами jwt(String)(///добавлю позже параметр datetime -последнее принятое сообщение )
Возращает  в виде json команды, содержащие пользователей и массивов из данных по каждому пользователю///надо проверить ещё



