<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:template match="/">
 <html>
   <head>
   <style>
table {
    border-collapse:separate;
    border:solid black 2px;
    border-radius:6px;
    -moz-border-radius:6px;
}

td, th {
    border-left:solid black 2px;
    border-top:solid black 2px;
    text-align:center;
}

th {
    background-color: red;
    border-top: none;
    color : white;
}

td:first-child {
     border-left: none;
}

td:last-child {
       text-align:left;
       }

h1 {
  text-align: center;
}

li {
  margin-top:2px;
}
       body {
       background: lightblue;
       }
   </style>
   </head>
   <body>
    <a name="top"></a>
    <h1>BugClerk Report</h1>
    <table align="center">
     <tr>
      <th>BZ</th>
      <th>Acks</th>
      <th>Release</th>
      <th>Violation</th>
     </tr>
     <xsl:for-each select="//bz">
      <tr>
        <td>
          <xsl:element name="a">
            <xsl:attribute name="href">
              <xsl:value-of select="@href"/>
            </xsl:attribute>
            <xsl:value-of select="@id" />
          </xsl:element>
       </td>
       <td>
         <xsl:value-of select="@acks"/>
       </td>
       <td>
         <xsl:value-of select="@release"/>
       </td>
       <td>
           <ul>
              <xsl:for-each select="./violation">
                  <li><b>[<xsl:value-of select="@severity"/>]</b><xsl:text> </xsl:text><em><xsl:value-of select="@checkname"/></em> : <xsl:value-of select="@message"/><br/></li>
              </xsl:for-each>
           </ul>
       </td>
      </tr>
     </xsl:for-each>
     <p align="right"><a href="https://github.com/jboss-set/bug-clerk">BugClerk GH Repository</a></p>
    </table>
    <br/>
    <p align="right"><a href="#top">Back to top</a></p>
   </body>
  </html>
 </xsl:template>
</xsl:stylesheet>
