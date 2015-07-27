<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:template match="/">
 <html>
   <head>
   <style>
table {
    border-collapse:separate;
    border:solid black 1px;
    border-radius:6px;
    -moz-border-radius:6px;
}

td, th {
    border-left:solid black 1px;
    border-top:solid black 1px;
}

th {
    background-color: red;
    border-top: none;
    color : white;
}

td:first-child {
     border-left: none;
}

h1 {
  text-align: center;
}
   </style>
   </head>
   <body>
       <h1><a href="https://github.com/jboss-set/bug-clerk">BugClerk Report</a></h1>
    <table>
     <tr>
      <th>BZ</th>
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
          <br/>
          <xsl:value-of select="@acks"/>
          <br/>
          <xsl:value-of select="@release"/>
       </td>
       <td>
          <ul>
           <xsl:for-each select="./violation">
               <li>[<xsl:value-of select="@severity"/>] <em><xsl:value-of select="@checkname"/></em> : <xsl:value-of select="@message"/></li>
           </xsl:for-each>
          </ul>
       </td>
      </tr>
     </xsl:for-each>
    </table>
   </body>
  </html>
 </xsl:template>
</xsl:stylesheet>
