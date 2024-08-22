<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <meta charset="UTF-8">
      <title>My Home Page</title>
<style>
 @media only screen and (max-width: 767px) {
            body {
               width: 100%; /* Make the body full width on small screens */
               padding: 10px; /* Adjust padding for smaller screens */
            }
  .mobile-button-container {
               text-align: center;
               padding-top: 20px;
               padding-bottom: 30px;
            }

  .mobile-image {
               max-width: 100%;
               height: auto;
             
            }
 }
</style>
   </head>
   <body style="
      background-size: cover;
      background-repeat: no-repeat;
      background-position: center;
      background-blend-mode: multiply, normal;
      box-shadow: 0px 2px 8px 2px rgba(112, 128, 144, 0.12) !important;
      padding: 30px;
      width="719px">
      <div>
         <div>
            <div class="col-md-12 d-flex justify-content-end">
             
            </div>
            <h2 style="font-size: 30px; font-family: 'Jost', Arial, sans-serif; font-weight: 150;">Expiring Alert!</h2>

            <table class="table" style="width: 100%; table-layout: fixed;">
               <thead style="background-color: #F5F5F5; text-align: start;">
                     <tr>
                        <th scope="col" style="text-align: start;">Asset Id</th>
                        <th scope="col" style="text-align: start;">Name</th>
                        <th scope="col" style="text-align: start;">Expired Date</th>
                     </tr>
               </thead>
               <tbody>
                     <#list asset as item>
                        <tr>
                           <td style="text-align: start; padding: 5px;">${item.assetId}</td>
                           <td style="text-align: start; padding: 5px;">${item.name}</td>
                           <td style="text-align: start; padding: 5px;">${item.expiryDate}</td>
                        </tr>
                     </#list>
               </tbody>
            </table>
         </div>
      </div>
   </body>
</html>