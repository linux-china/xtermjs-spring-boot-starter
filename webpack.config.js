const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');

module.exports = {
    mode: "production",
    entry: './src/main/frontend/console.js',
    output: {
        path: path.resolve(__dirname, 'target/classes/META-INF/resources'),
        filename: 'xterm.bundle.js'
    },
    plugins: [
        new CleanWebpackPlugin()
    ],
    module: {
        rules: [
            {
                test: /\.m?js$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env']
                    }
                }
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.(png|svg|jpg|gif)$/,
                use: [
                    'file-loader'
                ]
            },
            {
                test: /\.(woff|woff2|eot|ttf|otf)$/,
                use: [
                    'file-loader'
                ]
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.json', '.wasm', '.mjs']
    },
    devServer: {
        contentBase: [path.join(__dirname, 'target/classes/META-INF/resources'), path.join(__dirname, 'src/main/frontend')],
        compress: true,
        port: 9000
    }
};
