// import { APP_BASE_HREF } from '@angular/common';
// import { CommonEngine } from '@angular/ssr';
// import express from 'express';
// import { fileURLToPath } from 'node:url';
// import { dirname, join, resolve } from 'node:path';
// import AppServerModule from './src/main.server';

// // The Express app is exported so that it can be used by serverless Functions.
// export function app(): express.Express {
//   const server = express();
//   const serverDistFolder = dirname(fileURLToPath(import.meta.url));
//   const browserDistFolder = resolve(serverDistFolder, '../browser');
//   const indexHtml = join(serverDistFolder, 'index.server.html');

//   const commonEngine = new CommonEngine();

//   server.set('view engine', 'html');
//   server.set('views', browserDistFolder);

//   // Example Express Rest API endpoints
//   // server.get('/api/**', (req, res) => { });
//   // Serve static files from /browser
//   server.get('*.*', express.static(browserDistFolder, {
//     maxAge: '1y'
//   }));

//   // All regular routes use the Angular engine
//   server.get('*', (req, res, next) => {
//     const { protocol, originalUrl, baseUrl, headers } = req;

//     commonEngine
//       .render({
//         bootstrap: AppServerModule,
//         documentFilePath: indexHtml,
//         url: `${protocol}://${headers.host}${originalUrl}`,
//         publicPath: browserDistFolder,
//         providers: [{ provide: APP_BASE_HREF, useValue: baseUrl }],
//       })
//       .then((html) => res.send(html))
//       .catch((err) => next(err));
//   });

//   return server;
// }

// function run(): void {
//   const port = process.env['PORT'] || 4200;

//   // Start up the Node server
//   const server = app();
//   server.listen(port, () => {
//     console.log(`Node Express server listening on http://localhost:${port}`);
//   });
// }

// run();
// server.ts
import { APP_BASE_HREF } from '@angular/common';
import { CommonEngine } from '@angular/ssr';
import express from 'express';
import { fileURLToPath } from 'node:url';
import { dirname, join, resolve } from 'node:path';
import AppServerModule from './src/main.server';

// The Express app is exported so that it can be used by serverless Functions.
export function app(): express.Express {
  const server = express();
  const serverDistFolder = dirname(fileURLToPath(import.meta.url));
  const browserDistFolder = resolve(serverDistFolder, '../browser');
  const indexHtml = join(serverDistFolder, 'index.server.html');

  const commonEngine = new CommonEngine();

  server.set('view engine', 'html');
  server.set('views', browserDistFolder);

  // Ejemplo de log al recibir una petición
  server.get('*', (req, res, next) => {
    if (req.originalUrl.startsWith('/api')) {
    return next();}
    console.log(`[SSR Request] URL: ${req.originalUrl}`);
    const { protocol, originalUrl, baseUrl, headers } = req;

    commonEngine
      .render({
        bootstrap: AppServerModule,
        documentFilePath: indexHtml,
        url: `${protocol}://${headers.host}${originalUrl}`,
        publicPath: browserDistFolder,
        providers: [{ provide: APP_BASE_HREF, useValue: baseUrl }],
      })
      .then((html) => {
        // Log si el renderizado fue exitoso
        console.log(`[SSR Success] URL: ${req.originalUrl}`);
        res.send(html);
      })
      .catch((err) => {
        // Log del error durante el renderizado
        console.error(`[SSR Error] URL: ${req.originalUrl}`, err);
        next(err);
      });
  });

  return server;
}

function run(): void {
  const port = process.env['PORT'] || 8443;

  // Start up the Node server
  const server = app();
  server.listen(port, () => {
    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

run();