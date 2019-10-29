/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
const React = require('react');
const ReactDOM = require('react-dom');
const expect = require('expect');
const TestUtils = require('react-dom/test-utils');
const {FormGroup} = require('react-bootstrap');
const Select = require('react-select');

const Catalog = require('../Catalog.jsx');

describe('Test Catalog panel', () => {
    beforeEach((done) => {
        document.body.innerHTML = '<div id="container"></div>';
        setTimeout(done);
    });
    afterEach((done) => {
        ReactDOM.unmountComponentAtNode(document.getElementById("container"));
        document.body.innerHTML = '';
        setTimeout(done);
    });
    it('creates the component with defaults', () => {
        const item = ReactDOM.render(<Catalog />, document.getElementById("container"));
        const catalog = TestUtils.findRenderedDOMComponentWithClass(item, "ms2-border-layout-body catalog");
        expect(item).toExist();
        expect(catalog).toExist();
    });
    it('test the search of records', (done) => {
        const item = ReactDOM.render(<Catalog
            services={{"csw": {
                type: "csw",
                url: "url",
                title: "csw"
            }}}
            selectedService="csw"
            onSearch={(props) => {
                expect(props).toExist();
                expect(props).toEqual({ format: 'csw', url: 'url', startPosition: 1, maxRecords: 4, text: '' } );
                done();
            }}
        />, document.getElementById("container"));
        expect(item).toExist();
        const buttons = TestUtils.scryRenderedDOMComponentsWithTag(item, "button");
        expect(buttons.length).toBe(1);
        const searchButton = buttons[0];
        TestUtils.Simulate.click(searchButton);
    });
    it('test service format', () => {
        const formatOptions = [{
            label: "image/png8",
            value: "image/png8"
        }, {
            label: "image/jpeg",
            value: "image/jpeg"
        }];
        const item = ReactDOM.render(<Catalog
            services={{"csw": {
                type: "csw",
                url: "url",
                title: "csw",
                format: "image/png8"
            }}}
            selectedService="csw"
            formatOptions={formatOptions}
            mode="edit"
            newService={{
                url: "url",
                type: "csw",
                title: "csw",
                oldService: "csw",
                showAdvancedSettings: true,
                format: "image/png8"
            }}
        />, document.getElementById("container"));

        expect(item).toExist();

        const formatFormGroups = TestUtils.scryRenderedComponentsWithType(item, FormGroup).filter(fg => {
            const labels = TestUtils.scryRenderedDOMComponentsWithTag(fg, 'label');
            return labels.length === 1 && labels[0].textContent === 'Format';
        });
        expect(formatFormGroups.length).toBe(1);
        const formatSelect = TestUtils.findRenderedComponentWithType(formatFormGroups[0], Select);
        expect(formatSelect).toExist();
        expect(formatSelect.props.value).toBe('image/png8');
        expect(formatSelect.props.options).toEqual(formatOptions);
    });
});
