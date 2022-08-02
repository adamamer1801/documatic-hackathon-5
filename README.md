<p align="center">
<h1>C-- Compiler</h1>
<h2>It's like C++, but written backwards. and flipped.</h2>
<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="114" height="20" role="img" aria-label="practicality: failing"><title>practicality: failing</title><linearGradient id="s" x2="0" y2="100%"><stop offset="0" stop-color="#bbb" stop-opacity=".1"/><stop offset="1" stop-opacity=".1"/></linearGradient><clipPath id="r"><rect width="114" height="20" rx="3" fill="#fff"/></clipPath><g clip-path="url(#r)"><rect width="71" height="20" fill="#555"/><rect x="71" width="43" height="20" fill="#e05d44"/><rect width="114" height="20" fill="url(#s)"/></g><g fill="#fff" text-anchor="middle" font-family="Verdana,Geneva,DejaVu Sans,sans-serif" text-rendering="geometricPrecision" font-size="110"><text aria-hidden="true" x="365" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="610">practicality</text><text x="365" y="140" transform="scale(.1)" fill="#fff" textLength="610">practicality</text><text aria-hidden="true" x="915" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="330">failing</text><text x="915" y="140" transform="scale(.1)" fill="#fff" textLength="330">failing</text></g></svg>

<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="106" height="20" role="img" aria-label="usefulness: none"><title>usefulness: none</title><linearGradient id="s" x2="0" y2="100%"><stop offset="0" stop-color="#bbb" stop-opacity=".1"/><stop offset="1" stop-opacity=".1"/></linearGradient><clipPath id="r"><rect width="106" height="20" rx="3" fill="#fff"/></clipPath><g clip-path="url(#r)"><rect width="69" height="20" fill="#555"/><rect x="69" width="37" height="20" fill="#9f9f9f"/><rect width="106" height="20" fill="url(#s)"/></g><g fill="#fff" text-anchor="middle" font-family="Verdana,Geneva,DejaVu Sans,sans-serif" text-rendering="geometricPrecision" font-size="110"><text aria-hidden="true" x="355" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="590">usefulness</text><text x="355" y="140" transform="scale(.1)" fill="#fff" textLength="590">usefulness</text><text aria-hidden="true" x="865" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="270">none</text><text x="865" y="140" transform="scale(.1)" fill="#fff" textLength="270">none</text></g></svg>

<h2>About</h2>
C-- is C++ but reversed and flipped. Yes it just reverses the code and compiles it using a regular C++ compiler, but I read that we were graded on quality. Granted this code is probably not quality either. You can check the examples folder for C-- code, C++ code, Windows executables, and Unix (Linux/MacOS) binaries.

But to make it a bit more "quality", I added an online compiler which compiles Windows executables, over HTTP.

<br /> <br />

<h2>How to run</h2>
<h3>Jarfile is in dist folder</h3>
I tried to convert the .jar into the binaries for Windows, MacOS and Linux but all the Java wrappers are either paid, outdated, or complicated so just run the jarfile with <code>java -jar cli.jar [..arguments]</code>.
<br><br>
<h3> Normal person </h3>
<ol>
    <li> Download the jarfile </li>
    <li> Write a C++ program </li>
    <li> Convert it to a C-- program using <code>java -jar cli.jar ToCMM file.cpp</code></li>
    <li> Compile the file using <code>java -jar cli.jar compile file.cmm</code></li>
</ol>
<h3> Psychopath edition </h3>
<ol>
    <li> Download the jarfile </li>
    <li> Write a C-- program </li>
    <li> Compile the file using <code>java -jar cli.jar compile file.cmm</code></li>
</ol>

<br /><br />

<h2>Subcommands</h2>
So we have 3 sub-commands, `ToCMM`, `FromCMM`, and `compile`
<ul>
<li> <b>ToCMM</b>: ToCMM just turns C++ code into C-- code 
<ul>
    <li> <code>-o</code>: Specify output of conversion </li>
    <li> <code>-es (encoding)</code>: Encodes all your strings in your program with the provided encoding</li>
</ul>
</li>
<li> <b>FromCMM</b>: FromCMM turns C-- code into C++ code 
<ul>
    <li> <code>-o</code>: Specify output of conversion </li>
</ul>
</li>
<li> <b>compile</b>: Compiles the C-- code 
<ul>
    <li> <code>-o</code>: Specify output of compilation </li>
    <li> <code>-on</code>: Whether to use online compiling or not</li>
    <li> <code>-c</code>: Which compiler to use (e.g. g++)</li>
</ul>
</li>
</ul>
<br /><br />
<h2>Online Compiler</h2>
So a few hours into this project I realised that C++ compilers are stupid and annoying and i hate them

Although the CLI does support local compiling, it also has an online compiler which compiles code on a server, and returns the code. It only works for Windows (64bit) operating systems, but it works.

also it doesn't work with includes not built in with C++
<br /><br />

<h2>List of local compilers / platform</h2>
<h4>Windows:</h4>
<ul>
<li>Mingw-w64 (not tested)</li>
</ul>
<h4>Linux/MacOS (unix):</h4>
<ul>
<li>make</li>
<li>g++</li>
</ul>

<br /><br />

<h2>Just a note (module usage)</h2>
In the Java CLI, I used a snippet of FilenameUtils and IOUtils from Apache CommonsIO. I took them from their module since the compiled cli would've been 2MB, removing CommonsIO removed 1.5MB, making the size ~400KB. I also used PicoCLI to avoid parsing the CLI arguments myself. I tried it but the code was just too messy and since it will be judged on quality, I don't see how that would suffice.
<br>
In the (Golang) online compiler, I used the Gin framework for HTTP, and a bunch of _built-in_ modules for strings, logging, input/output, etc.
<br>
The online compiler is also hosted under https://compile.cmm.danky.dev/ and is integrated into the CLI.
